# kotlin-aws-jamstack

`kotlin-aws-jamstack` is a template for writing a [jamstack](https://jamstack.org/) application
using Kotlin and serverless-friendly AWS technologies.

There's a frontend module that uses kotlinx.html to generate .html files that are then uploaded
to S3 and served via CloudFront. [docs](https://docs.aws.amazon.com/AmazonS3/latest/dev/WebsiteHosting.html)

There's a backend module that uses simple routing and interceptor logic to process requests on Lambda.
You write your own controllers, which process requests into responses.

Finally, there's an infrastructure module that uses the CDK to wire everything up and deploy to your AWS account.

## Buzzwords

If these catch your eye, this project might be for you.

* Kotlin 1.4
  * kotlinx.html
  * kotlinx.serialization
* Sass
* AWS
  * S3
  * Lambda
  * API Gateway
  * CDK
* Koin
* jackson-jr
* kotlin-logging, log4j2, slf4j
* JUnit 5
* AssertJ
* MockK
* Gradle (+ build.gradle.kts)
* Proguard
* IntelliJ

## What is "Jamstack"?

It's an acronym:
* J: JavaScript
* A: API
* M: Markup

You use a static site generator of some sort to generate the markup, and then use JavaScript to dynamically update the
site by hitting APIs (microservices, if you like). It's basically the same as a regular dynamic site, except all the 
HTML is pre-generated, which means it can be served from CDNs.

You can also Google the term...I didn't come up with it, but official sites do seem highly branded and bury the lede
somewhat.

## Prerequisites

This project assumes you have JDK11 installed on your system. The directions for installing it vary widely from system
to system, but you should be able to run `java -version` and see output indicating JDK11 or later.

This assumes you have the following additional CLI applications installed and on your path:
* [`aws`](https://aws.amazon.com/cli/): AWS CLI. Required for managing AWS credentials.
* [`cdk`](https://docs.aws.amazon.com/cdk/latest/guide/cli.html): AWS CDK. Required for pushing code to AWS.
* [`sass`](https://sass-lang.com/): Sass. Required to transpile .scss files to .css.

Also...there's currently no Windows support. Various Gradle tasks shell out to the command line, but currently
these tasks only work on Unix-like systems.

### `aws`

Run `aws configure list`. If you see `access_key` and `secret_key`, you're good to go.

If not, follow [these steps](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html).

### `cdk`

Run `cdk --version` -- it should print out something like `1.78.0 (build 2c74f4c)`.

If not, follow [these steps](https://docs.aws.amazon.com/cdk/latest/guide/cli.html).

### `sass`

Run `sass --version` -- it should print out something like `1.29.0 compiled with dart2js 2.10.3`.

If not, follow [the Command Line directions](https://sass-lang.com/install).

## Modules

There are three modules that make up this project.

### Frontend

The `frontend` module is essentially a DIY CLI application for generating HTML, CSS, and JS.

The HTML is generated using [kotlinx.html](https://github.com/Kotlin/kotlinx.html). The main advantage here is
you get to write in strongly-typed Kotlin, and reusing code and HTML is easy. The main disadvantage is if you're
hoping to reuse templates or themes, you won't really be able to do that without porting it over to Kotlin.

The CSS is generated by Sass.

The JS is just copied, but TypeScript support would be better here. Kotlin/JS support is not planned.

### Backend

The `backend` module contains a Kotlin Lambda handler and some basic routing logic. It uses Koin, jackson-jr,
kotlinx.serialization, and other libraries to make this easier for you to organize.

Tests are handled by JUnit-5, MockK, and AssertJ.

### CDK

The `cdk` module sets up all the infrastructure; it deploys your frontend and backend code, and sets up S3
buckets, Lambdas, CloudFront distributions, etc.

# Getting Started

> :checkered_flag: This repo is a template, meaning it's not a library, and you're not intended to fork it.

Click the `Use this template` button near the top of the GitHub repo. This will prompt you to create a new repo;
settings can be whatever you want. If you're not ready for that level of commitment, you can also choose the
`Code > Download` dropdown option.

Next, double-check that your [prerequisites](#prerequisites) are set up and configured.

After cloning your templated repo or unpacking the .zip, open up your new project in IntelliJ and take a look around. 
There are three main subdirectories to take note of: `backend`, `frontend`, and `cdk`, which are modules documented above.

You are now ready to deploy the stack to your AWS account.

> :money_with_wings: Deploying to your account will likely incur a cost, likely a very small cost, for things like S3
> storage, on the order of USD pennies per month. Many things, like Lambda invocations, may fall well within AWS's free 
> tier.

Just run `./gradlew deploy` from the root directory; this will create three CloudFormation stacks starting with 
`KotlinAwsJamStack`.

When you perform the deployment, there will likely be quite the wall of text, but there are two points of interest: the
exports from the backend stack, and the exports from the frontend stack. The backend stack will export the path to the
Lambda (well, the API Gateway fronting the Lambda). This is the "API Root". The frontend stack exports the S3 Bucket path
(technically the CloudFront Distribution URL) which you can hit to load the site.

Before loading the site, you currently need to manually configure the frontend to be able to reach the backend API. Copy
the exported path from the backend stack, it should look like this:
```text
Outputs:
KotlinAwsJamStackbackendstack78638519.backendrestlambdarestapiEndpointFE7DA095 = https://tk.execute-api.us-west-2.amazonaws.com/prod/
                                                 this is the API root, copy this ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
```
Then navigate to [UrlConfig](frontend/src/main/kotlin/config/UrlConfig.kt) and replace `apiRoot` with that value.

Now the frontend will be able to call the API. Execute `./gradlew deploy` again.

Look for the output from the frontend stack, it should look like this:
```text
Outputs:
KotlinAwsJamStackfrontendstack139BFCAD.frontendbucketKotlinAwsJamStackfrontendbucketF33D241F = http://tk.s3-website-us-west-2.amazonaws.com
```

The URL at the end is where you can access the new site. Give it a try!

That's it for the setup -- you now have a working simple application.

For more information, see the wiki.

# Teardown

Want to remove everything from your AWS account related to `kotlin-aws-jamstack`?
1. Navigate to the CloudFormation section of your AWS account.
2. Identify the stacks you want to remove; by default, they start with `KotlinAwsJamstack`.
3. Click on the `frontend` stack and click delete, and wait for it to complete.
4. Repeat that step for the `backend` stack. 
5. Repeat one last time for the top-level stack (no suffix).
6. This will delete most of your resources, but not all of them, per their DeletionPolicy settings. If you have data in 
   S3, DynamoDB, or RDS, these may not be deleted.
7. The S3 bucket that contains the website will, by default, not be deleted when its stack is deleted.
  * If you want to delete it, you'll need to find and delete it manually; it should start with `kotlinawsjamstackfrontend`.

# Performance

Performance isn't a core part of this project, but you might be curious anyway. There are several provided controllers
that can be useful for baseline sanity tests.

* ClockController, cold start: 530ms + 800ms Init
* ClockController, second request: 2.7ms
* ReverseStringController, first request: 40ms
* ReverseStringController, second request: 3.0ms

Obviously this number will increase as you add more dependencies and such, but as a baseline, cold start is under 2
seconds and second requests are under 5ms.

# How do I update?

Short answer: you don't.

Long answer...I haven't given a lot of thought to this. I really like the idea of this being an "unframework" -- it's
simple, and everything is just _right there_ for you to tweak and modify as you see fit. However, this does mean it's a
tricky manual process to update your template to match the latest changes upstream.

Alternatively, you can always fork this repo and use git to merge in changes from upstream into your fork, though
obviously there could still be conflicts as there's no real version management this way.

# Licensing

See [UNLICENSE](UNLICENSE). You have permission to change the license for your copy of this repo.

Original repo: https://github.com/nanodeath/kotlin-aws-jamstack
