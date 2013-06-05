Getting Started: Consuming REST Services with Spring
====================================================

What you'll build
-----------------

This Getting Started guide will walk you through the process of consuming a REST service using Spring for Android's `RestTemplate`.

What you'll need
----------------

- About 15 minutes
 - A favorite text editor or IDE
 - [JDK 6][jdk] or later
 - [Maven 3.0][mvn] or later

[jdk]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[mvn]: http://maven.apache.org/download.cgi

How to complete this guide
--------------------------

Like all Spring's [Getting Started guides](/getting-started), you can start from scratch and complete each step, or you can bypass basic setup steps that are already familiar to you. Either way, you end up with working code.

To **start from scratch**, move on to [Set up the project](#scratch).

To **skip the basics**, do the following:

 - [Download][zip] and unzip the source repository for this guide, or clone it using [git](/understanding/git):
`git clone https://github.com/springframework-meta/{@project-name}.git`
 - cd into `{@project-name}/initial`
 - Jump ahead to [Create a resource representation class](#initial).

**When you're finished**, you can check your results against the code in `{@project-name}/complete`.

<a name="scratch"></a>
Installing the Android Development Environment
----------------------------------------------

Building Android applications requires the installation of the [Android SDK].

### Install the Android SDK

1. Download the correct version of the [Android SDK] for your operating system from the Android web site.

2. Unzip the archive and place it in a location of your choosing. For example on Linux or Mac, you may want to place it in the root of your user directory. See the [Android Developers] web site for additional installation details.

3. Configure the `ANDROID_HOME` environment variable based on the location where you installed the Android SDK. Additionally, you should consider adding `ANDROID_HOME/tools`, `ANDROID_HOME/platform-tools`, and `ANDROID_HOME/build-tools` to your PATH.

	Mac OS X:

	```sh
	$ export ANDROID_HOME=/<installation location>/android-sdk-macosx
    $ export PATH=${PATH}:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
    ```
    
    Linux:
    
    ```sh
    $ export ANDROID_HOME=/<installation location>/android-sdk-linux
    $ export PATH=${PATH}:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
    ```
	    
    Windows:
    
    ```sh
    set ANDROID_HOME=C:\<installation location>\android-sdk-windows
    set PATH=%PATH%;%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools
    ```

4. Once the SDK is installed, we need to add the relevant [Platforms and Packages]. We are using Android 4.2.2 (API 17) in this guide.

### Install Android SDK Platforms and Packages

The Android SDK download does not include any specific Android platform SDKs. In order to run the sample code you need to download and install the latest SDK Platform. You accomplish this by using the Android SDK and AVD Manager that was installed from the previous step.

1. Open the Android SDK Manager window:

	```sh
	$ android
	```

	> Note: if this command does not open the Android SDK Manager, then your path is not configured correctly.
	
2. Select the checkbox for *Tools*

3. Select the checkbox for the latest Android SDK, "Android 4.2.2 (API 17)" as of this writing

4. Select the checkbox for the *Android Support Library* from the *Extras* folder

5. Click the **Install packages...** button to complete the download and installation.

	> Note: you may want to simply install all the available updates, but be aware it will take longer, as each SDK level is a sizable download.

### Create an Android Virtual Device

If you have never installed the Android SDK, then you will probably need to create a new Android Virtual Device (avd) in order to use the emulator later on.

1. Check if there are any avds

    android list avd
    
2. If there are no devices listed, then create one:

    android create avd -n gs -t 1
    
3. If you get prompted about multiple system images, then pick one based on your platform. For example, I have an x86 laptop so I picked x86.

    android create avd -n gs -t 1 --abi x86
    
4. Check if you're new avd is on the list:

    android list avd
    
5. Test out your new avd by launching the emulator.

    emulator -avd gs

Set up the project
------------------

First you set up a basic build script. You can use any build system you like when building apps with Spring, but the code you need to work with [Maven](https://maven.apache.org) and [Gradle](http://gradle.org) is included here. If you're not familiar with either, refer to [Getting Started with Maven](../gs-maven/README.md) or [Getting Started with Gradle](../gs-gradle/README.md).

In a project directory of your choosing, create the following subdirectory structure; for example, with `mkdir -p src/main/java/hello` on *nix systems:

    └── src
        └── main
            └── java
                └── org
                    └── hello


### Create a Maven POM

> **ERROR:** Section 'maven-project-setup-options' not found

`pom.xml`
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>org.hello</groupId>
	<artifactId>rest-android-complete</artifactId>
	<version>0.1.0</version>
	<packaging>apk</packaging>
	<name>rest-android-complete</name>

	<dependencies>
		<dependency>
			<groupId>com.google.android</groupId>
			<artifactId>android</artifactId>
			<version>4.1.1.4</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.android</groupId>
			<artifactId>spring-android-rest-template</artifactId>
			<version>1.0.1.RELEASE</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
			<version>2.2.1</version>
		</dependency>
	</dependencies>

	<build>
		<sourceDirectory>src</sourceDirectory>
		<plugins>
			<plugin>
				<groupId>com.jayway.maven.plugins.android.generation2</groupId>
				<artifactId>android-maven-plugin</artifactId>
				<version>3.6.0</version>
				<configuration>
					<sdk>
						<platform>17</platform>
					</sdk>
					<deleteConflictingFiles>true</deleteConflictingFiles>
					<undeployBeforeDeploy>true</undeployBeforeDeploy>
       					<emulator>
            					<avd>gs</avd>
            					<!-- Wait for emulator starting (3 minutes) -->
            					<wait>180000</wait>
        				</emulator>
				</configuration>
				<extensions>true</extensions>
			</plugin>
			<plugin>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.1</version>
				<configuration>
					<source>1.6</source>
					<target>1.6</target>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>
```
    
<a name="initial"></a>
Create a representation class
-----------------------------

With the Android project configured, it's time to create our REST request. Before we can do that though, we need to consider the data we are wanting to consume. 

### Twitter JSON Data

In the case of Twitter search, JSON data is returned which looks like the following:

```json
{
    "completed_in": 0.036,
    "max_id": 326785345083568100,
    "max_id_str": "326785345083568130",
    "next_page": "?page=2&max_id=326785345083568130&q=gopivotal",
    "page": 1,
    "query": "gopivotal",
    "refresh_url": "?since_id=326785345083568130&q=gopivotal",
    "results": [
        {
            "created_at": "Tue, 23 Apr 2013 19:51:12 +0000",
            "from_user": "CetasAnalytics",
            "from_user_id": 353318641,
            "from_user_id_str": "353318641",
            "from_user_name": "Cetas",
            "geo": null,
            "id": 326785345083568100,
            "id_str": "326785345083568130",
            "iso_language_code": "en",
            "metadata": {
                "result_type": "recent"
            },
            "profile_image_url": "http://a0.twimg.com/profile_images/2269920546/p3949ch8l877idajhez6_normal.png",
            "profile_image_url_https": "https://si0.twimg.com/profile_images/2269920546/p3949ch8l877idajhez6_normal.png",
            "source": "&lt;a href=&quot;http://www.hootsuite.com&quot;&gt;HootSuite&lt;/a&gt;",
            "text": "Ready. Set. Go. Pivotal is now launching April 24th. Join the live broadcast at http://t.co/qqfl3zSXN3"
        }        
    ],
    "results_per_page": 15,
    "since_id": 0,
    "since_id_str": "0"
}
```

As you can see, Twitter returns quite a bit of information. Do not worry if some of this data appears unfamiliar. For the purposes of this guide, we are only going to concern ourselves with a few parts of it.

The `from_user` field represents the user who posted the tweet, and `text` is the actual text of the tweet. To model the tweet representation, we’ll create two representation classes which define these fields. In this example, we are making use of a few Jackson annotations. Jackson is a powerful JSON processor for Java, and can be utilized within Spring.

### Twitter Search Results

The first representation class defines a single property which contains the list of tweets from the search. The `@JsonIgnoreProperties` annotation says to ignore all the other fields in the JSON response data. We are only concerned with the tweets.


`src/main/java/org/hello/TwitterSearchResults.java`
```java
/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hello;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterSearchResults {

	private List<Tweet> results;

	public List<Tweet> getResults() {
		return results;
	}

	public void setResults(List<Tweet> results) {
		this.results = results;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Tweet tweet : results) {
			sb.append(tweet.getFromUser()).append(": ").append(tweet.getText()).append("\n");
		}
		return sb.toString();
	}

}
```
    

### Tweet

The second representation class is for each individual tweet. Again you see `@JsonIgnoreProperties` being used, and additionally, the `@JsonProperty` annotation allows allows us to map specific fields in the JSON data to fields in the representational class which have different names.

`src/main/java/org/hello/Tweet.java`
```java
/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hello;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {

	@JsonProperty("from_user")
	private String fromUser;

	private String text;

	public String getFromUser() {
		return fromUser;
	}
	
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

}
```
    

Invoking REST services with the RestTemplate
--------------------------------------------

Spring provides a convenient template class called the `RestTemplate`. The `RestTemplate` makes interacting with most RESTful services a one-liner incantation. In the example below, we establish a few variables and then make a request of the Twitter search service. As mentioned earlier, we will use Jackson to marshal the JSON response data into our representation classes.

`src/main/java/org/hello/HelloActivity.java`
```java
/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hello;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.os.Bundle;

public class HelloActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		String url = "http://search.twitter.com/search.json?q={query}";

		String queryParameter = "@gopivotal";

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		TwitterSearchResults results = restTemplate.getForObject(url, TwitterSearchResults.class, queryParameter);
	}

}
```
    
Thus far, we've only used the HTTP verb `GET` to make calls, but we could just as easily have used `POST`, `PUT`, etc.


Building and Running the Client
-------------------------------

To build the code, first build it:

    mvn clean install
    
Next, to run the emulator, invoke:

    mvn android:emulator-start
    
After the emulator is up and running, you can deploy and run the Android code to see the results of the search:

    mvn android:deploy android:run
	
This will compile the Android app and then run it in the emulator.


Summary
-------

Congratulations! You have just developed a simple REST client using Spring.

There's more to building and working with REST APIs than is covered here.

[Android SDK]: http://developer.android.com/sdk/index.html
[Android Developers]:http://developer.android.com/sdk/installing/index.html
[Platforms and Packages]:http://developer.android.com/sdk/installing/adding-packages.html