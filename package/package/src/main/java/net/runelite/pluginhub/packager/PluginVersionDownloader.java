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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PluginVersionDownloader {
    private static final String GITHUB_API_BASE_URL = "https://api.github.com/repos/";
    private static final String REPO_OWNER = "runelite";
    private static final String REPO_NAME = "plugin-hub";
    private static final String FILE_PATH = "plugins";
    private static final String VERSION_NUMBER = System.getenv("API_FILES_VERSION");

    public static String getCommitForVersion(String version)
    {
        JSONArray jsonCommits = new JSONArray(getVersionCommits(version));

        ArrayList<Version> versionsList = new ArrayList<>();
        for (int i = 0; i < jsonCommits.length(); i++)
        {
            JSONObject jsonObject = jsonCommits.getJSONObject(i);
            String message = jsonObject.getJSONObject("commit").getString("message").toLowerCase().replace("bump to ", "").trim();
            String sha = jsonObject.getString("sha");

            versionsList.add(Version.parse(message));
            if(message.equals(version))
            {
                return sha;
            }
        }
        // TODO test if this works
        Version o = Version.parse(version);
        versionsList.add(o);
        versionsList.sort(Version::compareTo);
        int in = versionsList.indexOf(o);
        Version clostestVersion = versionsList.get(in == 0 ? 0 : in - 1);
        return getCommitForVersion(clostestVersion.toString());
    }
    public static void main(String[] args)
    {
        getPluginFilesForCommit(getCommitForVersion(VERSION_NUMBER));
    }
    public static String getVersionCommits(String version)
    {
        StringBuilder result = new StringBuilder();
        int page = 1;

        try
        {
            while (true)
            {
                String apiUrl = "https://api.github.com/repos/runelite/plugin-hub/commits?path=runelite.version&page=" + page;
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String fileLine;
                while ((fileLine = fileReader.readLine()) != null)
                {
                    result.append(fileLine);
                }
                fileReader.close();
                String jsonResult = result.toString().trim();
                if (jsonResult.contains(version))
                {
                    return jsonResult;
                }

                page++;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result.toString();
    }

    private static void getPluginFilesForCommit(String commitSha)
    {
        try
        {
            String fileContentUrl = GITHUB_API_BASE_URL + REPO_OWNER + "/" + REPO_NAME + "/contents/" + FILE_PATH + "?ref=" + commitSha;
            URL fileContentUrlObj = new URL(fileContentUrl);
            HttpURLConnection fileConnection = (HttpURLConnection) fileContentUrlObj.openConnection();
            fileConnection.setRequestMethod("GET");
            fileConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            int fileResponseCode = fileConnection.getResponseCode();
            if (fileResponseCode == HttpURLConnection.HTTP_OK)
            {
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileConnection.getInputStream()));
                StringBuilder fileResponse = new StringBuilder();
                String fileLine;
                while ((fileLine = fileReader.readLine()) != null)
                {
                    fileResponse.append(fileLine);
                }
                fileReader.close();

                List<String> pluginsToDownload = null;

                if (!"ALL".equals(System.getenv("FORCE_BUILD")) && !Strings.isNullOrEmpty(System.getenv("FORCE_BUILD")))
                {
                    pluginsToDownload = Arrays.stream(System.getenv("FORCE_BUILD").split(",")).collect(Collectors.toList());

                }

                FileUtils.cleanDirectory(Packager.PLUGIN_ROOT);
                JSONArray plugins = new JSONArray(fileResponse.toString());
                for (int j = 0; j < plugins.length(); j++)
                {
                    JSONObject jsonObject = plugins.getJSONObject(j);
                    String name = jsonObject.getString("name");

                    String message = jsonObject.getString("download_url");

                    if (pluginsToDownload == null || pluginsToDownload.contains(name))
                    {
                        saveFileFromUrl(message, Packager.PLUGIN_ROOT.getAbsolutePath());
                    }
                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
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
}
