In maven: `~/.m2/settings.xml`

```
<settings>
    <pluginGroups>
        <pluginGroup>com.spotify</pluginGroup>
    </pluginGroups>
</settings>
```

build:

`./mvnw install dockerfile:build`

run:

`docker run -p 8080:8080 -t video-file-inspector`

sample curl:

`curl -X PUT -F videoFile=@<path/to/video/file> localhost:8080/video`
