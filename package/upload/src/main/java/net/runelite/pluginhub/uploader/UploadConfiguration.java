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
import lombok.Getter;
import lombok.experimental.Accessors;
import okhttp3.HttpUrl;


@Getter
@Accessors(chain = true)
public class UploadConfiguration implements Closeable
{

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
		return this;
	}

	@Override
	public void close()
	{
	}
}
