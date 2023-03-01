# spring-boot-example
A simple Spring Boot demo application to demonstrate how to perform API testing with [cifuzz](https://github.com/CodeIntelligenceTesting/cifuzz). 
cifuzz is a CLI interface that makes it easy to develop, run, and analyze fuzz tests.

## Introduction
The demo application offers a single REST API endpoint `hello` that can call with a `name` parameter, and it replies
with `"Hello <name>!"`. We made the endpoint fail when the provided name is equal to `attacker` ignoring case. We will
demonstrate how you can use CIFuzz CLI with our JUnit 5 integration to test this application. We will also show how 
you can automatically find failing test case.

You can run the application as follows
```shell
mvn spring-boot:run
```
The endpoint can then be reached at `http://localhost:8080/hello` and can be called via `http://localhost:8080/hello?name=foo` 

## Unit tests
The project contains twp unit tests that test the endpoint with two names `Developer` and `Hacker`. This demonstrates how you would test your code using specific input triggering specific behavior. You can run the unit tests
```shell
mvn test
```

## Fuzz tests
While unit tests provide a great value to make sure that your code is functionally correct. 
However, there might be corner cases and interactions that you are not aware of that can cause security 
and reliability issues. To address these cases, you create a fuzz test, which a method 
annotated with `@FuzzTest` and at least one parameter. Using a single parameter of type
[FuzzedDataProvider](https://codeintelligencetesting.github.io/jazzer-docs/jazzer-api/com/code_intelligence/jazzer/api/FuzzedDataProvider.html), 
which provides utility functions to produce commonly used Java values, or `byte[]`. 
CI Fuzz will then continuously execute with method and provide new inputs that maximize 
code coverage and trigger interesting behavior in your application.

In this example, we provide a fuzz test that uses the fuzzer input as the `name` parameter 
for the `hellp` API. This way, CI Fuzz can explore the different possibilities of the parameter
that trigger interesting behaviors, and thus very fast will find issue by generating the
`"attacker"` name.
```java
@WebMvcTest()
public class GreeterApplicationTests {
    @Autowired private MockMvc mockMvc;

    @FuzzTest(maxDuration = "10s") 
    public void fuzzTestHello(FuzzedDataProvider data) throws Exception {
        // Initialization code
        String name = data.consumeRemainingAsString();
        mockMvc.perform(get("/hello").param("name", name));
    }
}
```

### Running your fuzz test
1. (Once) Install the CI Fuzz CLI. You can get the
   [latest release from GitHub](https://github.com/CodeIntelligenceTesting/cifuzz/releases/latest)
   or by running our install script:

    ```shell
    sh -c "$(curl -fsSL https://raw.githubusercontent.com/CodeIntelligenceTesting/cifuzz/main/install.sh)"
    ```
    If you are using Windows you can download the [latest release](https://github.com/CodeIntelligenceTesting/cifuzz/releases/latest/download/cifuzz_installer_windows.exe)
    and execute it.
2. Login to our [CI Fuzz App](https://app.code-intelligence.com/)
   
    ```shell
    cifuzz login
    ```
    This will create an API access token that the CLI uses to communicate with the CI Fuzz app. 
    When logged in, the CLI can provide more details about the findings including severity. 
    You will also be able to run your tests at scale in our SaaS.
3. Run the fuzz test with CI Fuzz. For that you just need to provide the test class containing the fuzz test.
   ```shell
   cifuzz run com.example.GreeterApplicationTests
   ```
   CI Fuzz will quickly generate a test case triggering the bug (aka crashing inputs). 
   This test case is saved as a resource in your project and will be automatic picked 
   up when you execute your normal unit tests. That is when you execute `mvn test`, 
   all your unit tests will be executed in addition the fuzz tests. In this scenario, 
   CI Fuzz will only execute the tests with the crashing inputs and inputs from the corpus it 
   has collected during fuzzing. This way you can ensure that you quickly test for
   regressions.

# Conclusion
In this short tutorial, we have shown how to use CI Fuzz CLI to test your API. `cifuzz`
offers many more features, and if you are interested simply `cifuzz help`.