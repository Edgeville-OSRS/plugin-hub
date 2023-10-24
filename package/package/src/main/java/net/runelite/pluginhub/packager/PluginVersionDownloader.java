package net.runelite.pluginhub.packager;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.module.ModuleDescriptor.Version;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PluginVersionDownloader {
    private static final String GITHUB_API_BASE_URL = "https://api.github.com/repos/";
    private static final String REPO_OWNER = "runelite";
    private static final String REPO_NAME = "plugin-hub";
    private static final String FILE_PATH = "plugins";
    private static final String VERSION_NUMBER = System.getenv("API_FILES_VERSION");

    public static void main(String[] args)
    {
        String commit = findCommitForVersion(VERSION_NUMBER);
        if (commit == null)
        {
            System.err.println("No commit found for version " + VERSION_NUMBER + "! Using default project files.");
            return;
        }
        downloadPluginFilesForCommit(commit);
    }

    /**
     * Checks if a JSONArray of commit data contains a version equal to or less than the input version
     *
     * @param array The JSONArray to check
     * @param version The version to check
     * @return Whether or not the array contains the version or a version less than it.
     */
    private static String getClosestCommit(JSONArray array, Version version)
    {
        for (int i = 0; i < array.length(); i++)
        {
            JSONObject jsonObject = array.getJSONObject(i);
            String message = jsonObject.getJSONObject("commit").getString("message").toLowerCase().replace("bump to ", "").trim();

            // Skip if the commit is reverting a version
            if (message.contains("revert"))
            {
                continue;
            }
            Version currentVersion = parseVersion(message);
            String sha = jsonObject.getString("sha");

            // Return if we've found the version or the closest version
            if(currentVersion.compareTo(version) <= 0)
            {
                System.out.println("Found closest version " + message);
                return sha;
            }
        }
        return null;
    }

    /**
     * Searches the commit history of runelite.version until the matching version number is found and returns
     * a JSONArray of the data. If no matching version is found it will return the entire commit history so that
     * the closest version can be used.
     *
     * @param version The version to search for
     * @return A JSONArray containing the commit history of runelite.version up until a matching version is found
     */
    public static String findCommitForVersion(String version)
    {
        Version desiredVersion = Version.parse(version);
        int page = 1;

        while (true)
        {
            String apiUrl = "https://api.github.com/repos/runelite/plugin-hub/commits?path=runelite.version&page=" + page;
            String response = getGithubApiJsonResponse(apiUrl);
            if (response != null)
            {
                JSONArray currentPageArray = new JSONArray(response);

                // Reached the last page without finding the version
                if (currentPageArray.isEmpty())
                {
                    break;
                }
                System.out.println(response);
                // We found the version we were searching for
                String closestVersionSha = getClosestCommit(currentPageArray, desiredVersion);
                if (closestVersionSha != null)
                {
                    return closestVersionSha;
                }
            }

            page++;
        }

        return null;
    }


    /**
     * Downloads the contents of the plugin folder at the given commit SHA
     *
     * @param commitSha The commit from which to clone the files
     */
    private static void downloadPluginFilesForCommit(String commitSha)
    {
        System.out.println("Grabbing files for " + commitSha);
        try
        {
            String fileContentUrl = GITHUB_API_BASE_URL + REPO_OWNER + "/" + REPO_NAME + "/contents/" + FILE_PATH + "?ref=" + commitSha;
            String response = getGithubApiJsonResponse(fileContentUrl);
            if (response != null)
            {

                List<String> pluginsToDownload = null;
                // Only download the plugin file for the plugins we selected to build
                if (!Strings.isNullOrEmpty(System.getenv("FORCE_BUILD")) && !"ALL".equals(System.getenv("FORCE_BUILD")))
                {
                    pluginsToDownload = Arrays.stream(System.getenv("FORCE_BUILD").split(",")).collect(Collectors.toList());

                }

                FileUtils.cleanDirectory(Packager.PLUGIN_ROOT);
                JSONArray plugins = new JSONArray(response);
                for (int j = 0; j < plugins.length(); j++)
                {
                    JSONObject fileObject = plugins.getJSONObject(j);
                    String pluginName = fileObject.getString("name");

                    String pluginFileUrl = fileObject.getString("download_url");

                    // If the plugin list is null we download all plugins, otherwise make sure the plugin is in our list
                    if (pluginsToDownload == null || pluginsToDownload.contains(pluginName))
                    {
                        saveFileFromUrl(pluginFileUrl, Packager.PLUGIN_ROOT.getAbsolutePath());
                    }
                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sends a GET request to the specified apiUrl and return the result as a JSON String.
     *
     * @param apiUrl The Github API URL to query
     * @return The query result
     */
    private static String getGithubApiJsonResponse(String apiUrl)
    {
        try
        {
            URL fileContentUrlObj = new URL(apiUrl);
            HttpURLConnection fileConnection = (HttpURLConnection) fileContentUrlObj.openConnection();
            fileConnection.setRequestMethod("GET");
            fileConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            int fileResponseCode = fileConnection.getResponseCode();
            if (fileResponseCode == HttpURLConnection.HTTP_OK)
            {
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileConnection.getInputStream()));
                StringBuilder fileResponse = new StringBuilder();
                String fileLine;
                while ((fileLine = fileReader.readLine()) != null) {
                    fileResponse.append(fileLine);
                }
                fileReader.close();
                return fileResponse.toString();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Downloads a file to the specified path
     *
     * @param fileUrl The url of the file to download
     * @param directoryPath The path to download the file to
     *
     * @throws IOException
     */
    private static void saveFileFromUrl(String fileUrl, String directoryPath) throws IOException
    {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK)
        {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/'));
            Path filePath = Path.of(directoryPath, fileName);
            File f = filePath.toFile();
            if(!f.exists())
            {
                f.mkdirs();
            }
            try (InputStream inputStream = connection.getInputStream())
            {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        else
        {
            throw new IOException("HTTP response code: " + responseCode);
        }
    }

    /**
     * Parses just the version number from a String that may contain other characters
     *
     * @param versionContainingString A String that contains a version number
     * @return The version number
     */
    private static Version parseVersion(String versionContainingString)
    {
        StringBuilder versionNumberBuilder = new StringBuilder();

        for (char c : versionContainingString.toCharArray())
        {
            if (Character.isDigit(c) || c == '.')
            {
                versionNumberBuilder.append(c);
            }
        }

        return Version.parse(versionNumberBuilder.toString());
    }
}
