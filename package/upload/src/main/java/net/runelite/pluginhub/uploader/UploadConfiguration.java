/*
 * Copyright (c) 2020 Abex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.pluginhub.uploader;

import com.google.common.base.Strings;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Getter;
import lombok.experimental.Accessors;
import okhttp3.HttpUrl;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;


@Getter
@Accessors(chain = true)
public class UploadConfiguration implements Closeable
{
//	private S3Client client;

	@Getter
	private HttpUrl versionlessRoot;

	private HttpUrl uploadRepoRoot;

	public UploadConfiguration fromEnvironment(String runeliteVersion)
	{
		String prNo = System.getenv("PACKAGE_IS_PR");
		if (prNo != null && !prNo.isEmpty() && !"false".equalsIgnoreCase(prNo))
		{
			return this;
		}

		setClient(System.getenv("REPO_CREDS"));

		String uploadRepoRootStr = System.getenv("REPO_ROOT");
		if (!Strings.isNullOrEmpty(uploadRepoRootStr))
		{
			versionlessRoot = HttpUrl.parse(uploadRepoRootStr);
			uploadRepoRoot = versionlessRoot
				.newBuilder()
				.addPathSegment(runeliteVersion)
				.build();
		}

		return this;
	}

	public boolean isComplete()
	{
		return true;
	}

	public UploadConfiguration setClient(String credentials)
	{
//		client = S3Client.builder()
//				.region(Region.US_EAST_1)  // Specify the desired AWS region
//				.build();


		return this;
	}

//	public void put(String bucketName, String key, File data) {
//		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//				.bucket(bucketName)
//				.key(key)
//				.build();
//
//		client.putObject(putObjectRequest, RequestBody.fromFile(data));
//	}
//
//	public void copy(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey) {
//		if (sourceBucketName.equals(destinationBucketName))
//		{
//			return;
//		}
//		CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
//				.sourceBucket(sourceBucketName)
//				.sourceKey(sourceKey)
//				.destinationBucket(destinationBucketName)
//				.destinationKey(destinationKey)
//				.build();
//
//		client.copyObject(copyObjectRequest);
//	}
//	public void mkdirs(String bucketName, String directoryPath) {
//		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//				.bucket(bucketName)
//				.key(directoryPath + "/") // Add a trailing slash to represent a directory
//				.build();
//
//		client.putObject(putObjectRequest, RequestBody.empty());
//	}

//	public void put(HttpUrl path, File data) throws IOException
//	{
//		try (Response res = client.newCall(new Request.Builder()
//				.url(path)
//				.put(RequestBody.create(null, data))
//				.build())
//			.execute())
//		{
//			Util.check(res);
//		}
//	}
//
//	public void copy(HttpUrl from, HttpUrl to, String resource, boolean mustExist) throws IOException
//	{
//		if (from.equals(to))
//		{
//			return;
//		}
//
//		try (Response res = client.newCall(new Request.Builder()
//				.url(from.newBuilder().addPathSegment(resource).build())
//				.method("COPY", null)
//				.header("Destination", to.newBuilder().addPathSegment(resource).build().toString())
//				.build())
//			.execute())
//		{
//			if (!mustExist && res.code() == 404)
//			{
//				return;
//			}
//
//			Util.check(res);
//		}
//	}
//
//	public void mkdirs(HttpUrl url) throws IOException
//	{
//		for (int i = 0; i < 2; i++)
//		{
//			try (Response res = client.newCall(new Request.Builder()
//					.url(url.newBuilder()
//						.addPathSegment("/")
//						.build())
//					.method("MKCOL", null)
//					.build())
//				.execute())
//			{
//				if (res.code() == 409 && i == 0)
//				{
//					mkdirs(url.newBuilder()
//						.removePathSegment(url.pathSize() - 1)
//						.build());
//
//					continue;
//				}
//
//				// even though 405 is method not allowed, if your webdav
//				// it actually means this url already exists
//				if (res.code() != 405)
//				{
//					Util.check(res);
//				}
//
//				return;
//			}
//		}
//	}

	@Override
	public void close()
	{
//		if (client != null)
//		{
//			client.connectionPool().evictAll();
//		}
	}
}
