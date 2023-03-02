<div align="center">
<a href="https://www.code-intelligence.com/">
<img src="https://www.code-intelligence.com/hubfs/Logos/CI%20Logos/Logo_quer_white.png" alt="Code Intelligence logo" width="450px">
</a>
</div>

# Spring Boot Example
A simple Spring Boot demo application to demonstrate how to perform API testing with [cifuzz](https://github.com/CodeIntelligenceTesting/cifuzz). 
cifuzz is a CLI interface that makes it easy to develop, run, and analyze fuzz tests.

## Introduction
The demo application offers a single REST API endpoint `hello` that can be called a `name` parameter, and it replies
with `"Hello <name>!"`. We made the endpoint fail when the provided name is equal to `attacker` ignoring case. We will
demonstrate how you can use CI Fuzz with our JUnit 5 integration to test this application. We will also show how 
you can automatically find the failing test case.

You can run the application as follows
```shell
mvn spring-boot:run
```
The endpoint can then be reached at `http://localhost:8080/hello` and can be called via `http://localhost:8080/hello?name=foo` 

## Unit tests
The project contains two unit tests that test the endpoint with two names `Developer` and `Contributor`. 
This demonstrates how you would test your code using specific inputs triggering specific behavior. 
You can run the unit tests
```shell
mvn test
```

## Fuzz tests
While unit tests provide a great value to make sure that your code is functionally correct. 
However, there might be corner cases and interactions that you are not aware of that can cause security 
and reliability issues. To address these cases, you create a fuzz test, which is a method 
annotated with `@FuzzTest` and at least one parameter. Using a single parameter of type
[FuzzedDataProvider](https://codeintelligencetesting.github.io/jazzer-docs/jazzer-api/com/code_intelligence/jazzer/api/FuzzedDataProvider.html), 
which provides utility functions to produce commonly used Java values, or `byte[]`. 
CI Fuzz will then execute with method in a loop and in each iteration provide new inputs that maximize 
code coverage and trigger interesting behavior in your application.

In this example, we provide a fuzz test that uses the fuzzer input as the `name` parameter 
for the `hello` API. This way, CI Fuzz can explore the different possibilities of the parameter
that trigger interesting behaviors, and thus very fast will find the prepared issue by generating the
`"attacker"` name.
```java
@WebMvcTest()
public class GreeterApplicationTests {
    @Autowired private MockMvc mockMvc;

    @FuzzTest 
    public void fuzzTestHello(FuzzedDataProvider data) throws Exception {
        // Initialization code
        String name = data.consumeRemainingAsString();
        mockMvc.perform(get("/hello").param("name", name));
    }
}
```

### Running your fuzz test
1. (Once) Install the CI Fuzz CLI named `cifuzz`. You can get the
   [latest release from GitHub](https://github.com/CodeIntelligenceTesting/cifuzz/releases/latest)
   or by running our install script:

    ```shell
    sh -c "$(curl -fsSL https://raw.githubusercontent.com/CodeIntelligenceTesting/cifuzz/main/install.sh)"
    ```
    If you are using Windows you can download the [latest release](https://github.com/CodeIntelligenceTesting/cifuzz/releases/latest/download/cifuzz_installer_windows.exe)
    and execute it.
2. Login to our [CI App](https://app.code-intelligence.com/)
   
    ```shell
    cifuzz login
    ```
    This will create an API access token that `cifuzz` uses to communicate with the CI App. 
    When logged in, the `cifuzz` can provide more details about the findings including severity. 
    You will also be able to run your tests at scale in our SaaS.
3. Run the fuzz test with CI Fuzz. For that you just need to provide the test class containing the fuzz test.
   ```shell
   > cifuzz run com.example.GreeterApplicationTests
   ▄ Build in progress... Done. 
   
   Running com.example.GreeterApplicationTests
   Storing generated corpus in .cifuzz-corpus/com.example.GreeterApplicationTests
   Starting from an empty corpus
   
   Use 'cifuzz finding <finding name>' for details on a finding.
   
   💥 [funny_sparrow] Security Issue: We panic when trying to greet an attacker! 
   in processRequest (org.springframework.web.servlet.FrameworkServlet:1014)
   
   Note: The reproducing inputs have been copied to the seed corpus at:

      src/test/resources/com/example/GreeterApplicationTestsInputs/funny_sparrow

   They will now be used as a seed input for all runs of the fuzz test,
   including remote runs with artifacts created via 'cifuzz bundle' and
   regression tests. For more information on regression tests, see:

       https://github.com/CodeIntelligenceTesting/cifuzz/blob/main/docs/Regression-Testing.md

   Execution time: 19s
   Average exec/s: 1682
   Findings:       1
   Corpus entries: 6 (+6)
   ```
   CI Fuzz will quickly generate a test case triggering the bug (aka crashing input).
   This test case is saved as a resource in your project and will be automatically picked
   up when you execute your normal unit tests. That is when you execute `mvn test`,
   all your unit tests will be executed in addition to the fuzz tests. In this scenario,
   CI Fuzz will only execute the tests with the crashing inputs and inputs from the corpus it
   has collected during fuzzing. This way you can ensure that you quickly test for
   regressions.
4. You can check the finding details as follows
   ```shell
   cifuzz finding funny_sparrow 
   ```
5. You can also check the code covered by CI Fuzz 
   ```shell
   > cifuzz coverage com.example.GreeterApplicationTests
   Building com.example.GreeterApplicationTests
   ▄  Build in progress... Done.
                    
   ✅ Coverage Report:
                                  File | Functions Hit/Found | Lines Hit/Found | Branches Hit/Found
   com/example/GreeterApplication.java |      2 / 3  (66.7%) |  4 / 6  (66.7%) |     2 / 2 (100.0%)
                                       |                     |                 |                   
                                       | Functions Hit/Found | Lines Hit/Found | Branches Hit/Found
                                 Total |               2 / 3 |           4 / 6 |              2 / 2

   ```
   In addition, you also get a `jacoco` coverage report that you can observe in your browser. 
   Having a look at coverage report helps understand the testing progress and observe the code
   areas that CI Fuzz has not yet covered. This is valuable so that you can improve and optimize
   your tests.

# Conclusion
In this short tutorial, we have shown how to use CI Fuzz CLI to test your API. `cifuzz`
offers many more features, and if you are interested simply `cifuzz help`.