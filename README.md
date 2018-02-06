# Kotlin AWS Lambda local execution example

This example uses [Gradle](https://gradle.org/) to actually build necessary files and place required jar files.

How to run:

```bash
# Build (compiled code can be found in `./build/docker`)
./gradlew build

# Use that directory to run compiled example
docker run --rm -v "$PWD/build/docker":/var/task lambci/lambda:java8 io.enjapan.examples.lambda.Hello '{"name": "Bob"}'
```

Should produce the following output:

```bash
START RequestId: aa0789b5-6149-4b99-84da-51cfd9a5a5e1 Version: $LATEST
END RequestId: aa0789b5-6149-4b99-84da-51cfd9a5a5e1
REPORT RequestId: aa0789b5-6149-4b99-84da-51cfd9a5a5e1	Duration: 1571.25 ms	Billed Duration: 1600 ms	Memory Size: 1536 MB	Max Memory Used: 8 MB

{"message":"Hello Bob"}
```

Using `run_lambda.sh` shell wrapper script one can run function with corresponding credentials for AWS SDK.
This can be useful if lambda function uses AWS clients and require certain permissions to function properly.

```bash
./gradlew build && ./run_lambda.sh io.enjapan.examples.lambda.Hello '{"name": "Bob"}'
```
