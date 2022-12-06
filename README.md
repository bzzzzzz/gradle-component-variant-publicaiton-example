The purpose of this repo is to demonstrate the unexpected behavior I see with 
the component variant publication in Gradle.

## Reproduction steps

To reproduce it, checkout the repository, and run `./gradlew publish`:

```
$ git clone git@github.com:bzzzzzz/gradle-component-variant-publicaiton-example.git
$ cd gradle-component-variant-publicaiton-example
$ ./gradlew publish
```

Once you do it, you'll see the following error:

```
> Task :lib:publishIvyPublicationToIvyRepository FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':lib:publishIvyPublicationToIvyRepository'.
> Failed to publish publication 'ivy' to repository 'ivy'
   > Invalid publication 'ivy': multiple artifacts with the identical name, extension, type and classifier ('lib', jar', 'jar', 'null').

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 1s
8 actionable tasks: 5 executed, 3 up-to-date
```

However, the artifact name for the variant should be `lib-custom`, not `lib` since the appendix is set
in the `ComponentModificationPlugin` that is applied.

## Expected result

The execution succeeds and generates the publication like this:

```
$ cat lib/build/repo/org.example/lib/1.2.3/ivy-1.2.3.xml
<?xml version="1.0" encoding="UTF-8"?>
<ivy-module version="2.0" xmlns:m="http://ant.apache.org/ivy/maven">
  <info organisation="org.example" module="lib" revision="1.2.3" status="integration" publication="20221206102603"/>
  <configurations>
    <conf name="additionalVariant" visibility="public"/>
    <conf name="compile" visibility="public"/>
    <conf name="default" visibility="public" extends="runtime"/>
    <conf name="runtime" visibility="public"/>
  </configurations>
  <publications>
    <artifact name="lib" type="jar" ext="jar" conf="compile,runtime"/>
    <artifact name="lib-custom" type="jar" ext="jar" conf="additionalVariant"/>
  </publications>
  <dependencies>
    <dependency org="org.apache.commons" name="commons-math3" rev="3.6.1" conf="compile-&gt;default"/>
    <dependency org="com.google.guava" name="guava" rev="31.0.1-jre" conf="runtime-&gt;default"/>
    <dependency org="org.apache.commons" name="commons-math3" rev="3.6.1" conf="runtime-&gt;default"/>
    <dependency org="org.apache.httpcomponents" name="httpclient" rev="4.5.3" conf="additionalVariant-&gt;default"/>
  </dependencies>
</ivy-module>
```
