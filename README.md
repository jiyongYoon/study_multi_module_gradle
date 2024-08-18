# Multi-Module 프로젝트

학습영상: [옥탑방개발자](https://youtu.be/1ZiBjduthSg?si=qsu0LgTLABwfcVcH)

## 1. 시작

```shell
ubuntu@jyyoon:/mnt/c/Users/c/Desktop/dev/yoon/study/java/multi-module-gradle$ mkdir file-management-
test
ubuntu@jyyoon:/mnt/c/Users/c/Desktop/dev/yoon/study/java/multi-module-gradle$ ls
file-management-test
ubuntu@jyyoon:/mnt/c/Users/c/Desktop/dev/yoon/study/java/multi-module-gradle$ cd file-management-test/
ubuntu@jyyoon:/mnt/c/Users/c/Desktop/dev/yoon/study/java/multi-module-gradle/file-management-test$ g
radle init
Starting a Gradle Daemon (subsequent builds will be faster)

Select type of build to generate:
  1: Application
  2: Library
  3: Gradle plugin
  4: Basic (build structure only)
Enter selection (default: Application) [1..4] 4

Project name (default: file-management-test):

Select build script DSL:
  1: Kotlin
  2: Groovy
Enter selection (default: Kotlin) [1..2] 2

Generate build using new APIs and behavior (some features may change in the next minor release)? (default: no) [yes, no]


> Task :init
To learn more about Gradle by exploring our Samples at https://docs.gradle.org/8.8/samples

BUILD SUCCESSFUL in 33s
1 actionable task: 1 executed

ubuntu@jyyoon:/mnt/c/Users/c/Desktop/dev/yoon/study/java/multi-module-gradle/file-management-test$ ll
total 16
drwxrwxrwx 1 ubuntu ubuntu 4096 Aug 16 22:59 ./
drwxrwxrwx 1 ubuntu ubuntu 4096 Aug 16 22:58 ../
-rwxrwxrwx 1 ubuntu ubuntu  214 Aug 16 22:59 .gitattributes*
-rwxrwxrwx 1 ubuntu ubuntu  103 Aug 16 22:59 .gitignore*
-rwxrwxrwx 1 ubuntu ubuntu  201 Aug 16 22:59 build.gradle*
drwxrwxrwx 1 ubuntu ubuntu 4096 Aug 16 22:59 gradle/
-rwxrwxrwx 1 ubuntu ubuntu 8706 Aug 16 22:59 gradlew*
-rwxrwxrwx 1 ubuntu ubuntu 2918 Aug 16 22:59 gradlew.bat*
-rwxrwxrwx 1 ubuntu ubuntu  355 Aug 16 22:59 settings.gradle*
```

### 구조

- gradle 최상위 객체 
  - rootProject
    - subProject1
    - subProject2...

- `settings.gradle`
  - 이 gradle이 어떤 구성으로 세팅되어 있는지
- `build.gradle`
  - 각 프로젝트 별로 매핑
  - 디렉토리 밑에 `build.gradle`만 있다고 프로젝트로 인식되는 것은 아님
    - /root/settings.gradle
      ```groovy
      rootProject.name = 'file-management-test'
      ```
    - /root/build.gradle
      ```groovy
      println '(/root) this project name = ' + project.name
      ```
    - /root/comp/build.gradle
      ```groovy
      println '(/root/comp) this project name = ' + project.name
      ```
    - /root/comp/tests/build.gradle
      ```groovy
      println '(/root/comp/tests/) this project name = ' + project.name
      ```
    - gradle build 시 console log
      ```text
      > Configure project :
      (/root) this project name = file-management-test
         
      > Task :prepareKotlinBuildScriptModel UP-TO-DATE
          
      Deprecated Gradle features were used in this build, making it incompatible with Gradle 9.0.
         
      You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.
       
      For more on this, please refer to https://docs.gradle.org/8.8/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.
      
      BUILD SUCCESSFUL in 191ms
      ```
  - `settings.gradle`에 어떤 모듈을 포함할지 세팅해주어야 추가됨
    - /root/settings.gradle
      ```groovy
      rootProject.name = 'file-management-test'
      include ":comp"
      include ":comp:tests"
      ```
    - /root/build.gradle
      ```groovy
      println '(/root) this project name = ' + project.name
      ```
    - /root/comp/build.gradle
      ```groovy
      println '(/root/comp) this project name = ' + project.name
      println '(/root/comp) this project parent name = ' + project.parent.name
      ```
    - /root/comp/tests/build.gradle
      ```groovy
      println '(/root/comp/tests/) this project name = ' + project.name
      println '(/root/comp/tests/) this project parent name = ' + project.parent.name
      ```
    - gradle build 시 console log
      ```text
      > Configure project :
      (/root) this project name = file-management-test
      
      > Configure project :comp
      (/root/comp) this project name = comp
      (/root/comp) this project parent name = file-management-test

      > Configure project :comp:tests
      (/root/comp/tests/) this project name = tests
      (/root/comp/tests/) this project parent name = comp
  
      > Task :prepareKotlinBuildScriptModel UP-TO-DATE
  
      Deprecated Gradle features were used in this build, making it incompatible with Gradle 9.0.
  
      You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.
  
      For more on this, please refer to https://docs.gradle.org/8.8/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.
  
      BUILD SUCCESSFUL in 299ms
      ```

> 프로젝트는 계층형 구조로 잘 생성되고 모듈도 잡힌다. <br>
> 하지만, 실제로 사용을 하면서 dependencies 들을 잡을 때 계층 구조를 모두 잡아줘야하는 등 번거로운 일이 많이 생긴다.
> 
> 단층 구조로 모듈을 만들고, `build.gradle` 파일을 통해 의존성을 주입해주자.

## 2. 효율적인 개선 구조

### Module 예시

- server: business service logic이 있는 모듈 => `@SpringBootTest` 통합테스트 진행
- web: api 호출이 있는 모듈 => `@WebMcvTest` 진행 => `server` 로직이 필요하며, 나를 의존하는 모듈들도 `server` 로직이 필요함
- jy: business domain logic이 있는 모듈 (DDD, 헥사고날 아키텍쳐 등에서 가장 핵심이 되는 모듈) => `@DataJpaTest` 진행. 가장 의존성이 적은 pure 모듈.

### 의존성

`jy` <- `web` <- `server`