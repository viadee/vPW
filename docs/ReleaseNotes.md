---
title: Release Notes
nav_order: 3
---
# Release notes

## 1.0.6

#### Version Look Up

| Typ                     | Name                     | Version                                                                   | Source                                                       |
| ----------------------- | ------------------------ |---------------------------------------------------------------------------| ------------------------------------------------------------ |
| Docker image            | vpw-analyzer             | **7.0.2**                                                                 | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-pipeline             | **7.0.2**                                                                 | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-frontend             | 7.0.1                                                                     | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-polling-client       | 2.0.1                                                                     | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Helm chart              | vpw-analyzer-chart       | **1.0.3**                                                                 | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-pipeline-chart       | **1.0.3**                                                                 | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-frontend-chart       | [1.0.2](https://github.com/viadee/charts/releases/tag/vpw-frontend-1.0.2) | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-polling-client-chart | 1.0.2                                                                     | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart (umbrella)   | vpw-chart                | [1.0.6](https://github.com/viadee/charts/releases/tag/vpw-chart-1.0.6)    | [viadee charts repository](https://github.com/viadee/charts) |


### Features
/
### Fixes
/

### Updates:

- Bump [camunda-kafka-model](https://github.com/viadee/camunda-kafka-polling-client) from 2.0.0 to 2.0.1. -> 47e3fc6d791b13760b83da10539471f69a148c01
- Bump [maven-project-info-reports-plugin](https://github.com/apache/maven-project-info-reports-plugin) from 3.2.1 to 3.2.2. -> ddf3f1c376fb8a5a9ecaf5ae709b337e5e107667
- Bump [spring-retry](https://github.com/spring-projects/spring-retry) from 1.3.1 to 1.3.2. -> 9e4e4fcb26b6da22eb829132b83812b0abb065bf
- Bump [hibernate-validator](https://github.com/hibernate/hibernate-validator) from 7.0.2.Final to 7.0.4.Final. -> 83ae080cea7a9e4dd859bb322c583a6fc5ec8230
- Bump [spring-aspects](https://github.com/spring-projects/spring-framework) from 5.3.15 to 5.3.17. -> c9b475eb36a72742f4c13b20a192828dbe4343a0
- Bump `spring-boot.version` from 2.5.4 to 2.6.5. -> 89afe4a33b9ecfab288641c9cd38d629163a07b3
- Bump [spring-boot-autoconfigure](https://github.com/spring-projects/spring-boot) from 2.5.6 to 2.6.6. -> f5deda13d31432a207d03b5cf058785d10aebb4b
- Bump [spring-aspects](https://github.com/spring-projects/spring-framework) from 5.3.17 to 5.3.18. -> 8786372001d624a2b53a212a16ef809127f43eb9
- Bump `elastic.version` from 7.15.1 to 7.17.2. -> d0a4a474a2ad503543bc605d06a3abd1086b9ed6
- Bump `spring-boot.version` from 2.6.5 to 2.6.6. -> af81ccede41dfeb366bde06b0749cbca781dcf26
- Bump [maven-surefire-plugin](https://github.com/apache/maven-surefire) from 3.0.0-M5 to 3.0.0-M6. -> bded5ac19d124d95223a6a7ccc870ea9b537707a
- Bump [camunda-bom](https://github.com/camunda/camunda-bpm-platform) from 7.16.0 to 7.17.0.


### Docs:

- [add Getting Started as User](https://github.com/viadee/vPW/commit/c90c1ef4cc6fafffb09c02b6b9cd48deb554bac1)

-------------------------------------------

## 1.0.5

#### Version Look Up

| Typ                     | Name                     | Version | Source                                                       |
| ----------------------- | ------------------------ | ------- | ------------------------------------------------------------ |
| Docker image            | vpw-analyzer             | 7.0.1   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-pipeline             | 7.0.1   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-frontend             | **7.0.1**   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-polling-client       | 2.0.1   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Helm chart              | vpw-analyzer-chart       | 1.0.2   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-pipeline-chart       | 1.0.2   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-frontend-chart       | [1.0.2](https://github.com/viadee/charts/releases/tag/vpw-frontend-1.0.2)   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-polling-client-chart | 1.0.2   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart (umbrella)   | vpw-chart                | [1.0.5](https://github.com/viadee/charts/releases/tag/vpw-chart-1.0.5)   | [viadee charts repository](https://github.com/viadee/charts) |


### Features
* Restructure nginx conf files: 
  
  Now it is possible to select between three configurations for the nginx:
    
    (1) local start-up for docker-compose
    
    (2) k8 start-up with basic auth (default)
    
    (3) k8 start-up without basic auth
  
* Adjust Dockerfile to recognize new nginx configuration possibilities 
* Adjust vpw-frontend-chart to be able to call [start-up script](https://github.com/viadee/charts/blob/main/charts/vpw-frontend-chart/values.yaml#L17) to choose between nginx configurations

### Fixes
/

### Updates:
/

-------------------------------------------

## 1.0.4
*Helm Chart Release*

#### Version Look Up

| Typ                     | Name                     | Version | Source                                                       |
| ----------------------- | ------------------------ | ------- | ------------------------------------------------------------ |
| Docker image            | vpw-analyzer             | 7.0.1   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-pipeline             | 7.0.1   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-frontend             | 7.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-polling-client       | 2.0.1   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Helm chart              | vpw-analyzer-chart       | [1.0.2](https://github.com/viadee/charts/releases/tag/vpw-analyzer-1.0.2)   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-pipeline-chart       | [1.0.2](https://github.com/viadee/charts/releases/tag/vpw-pipeline-1.0.2)   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-frontend-chart       | 1.0.1   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-polling-client-chart | 1.0.2   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart (umbrella)   | vpw-chart                | [1.0.4](https://github.com/viadee/charts/releases/tag/vpw-chart-1.0.4)   | [viadee charts repository](https://github.com/viadee/charts) |


### Features
* vpw-analyzer-chart and vpw-pipeline-chart are now able to use an external secret for the connection with elasticsearch.   

### Fixes
/

### Updates:
/

-------------------------------------------

## 1.0.3


#### Version Look Up

| Typ                     | Name                     | Version | Source                                                       |
| ----------------------- | ------------------------ | ------- | ------------------------------------------------------------ |
| Docker image            | vpw-analyzer             | 7.0.1   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-pipeline             | 7.0.1   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-frontend             | 7.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-polling-client       | [2.0.1](https://github.com/viadee/camunda-kafka-polling-client/releases/tag/v2.0.1)   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Helm chart              | vpw-analyzer-chart       | 1.0.1   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-pipeline-chart       | 1.0.1   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-frontend-chart       | 1.0.1   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-polling-client-chart | 1.0.2   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart (umbrella)   | vpw-chart                | 1.0.3   | [viadee charts repository](https://github.com/viadee/charts) |


### Features
/
### Fixes
* [set retry time for importing process model into analyzer to 10 minutes](https://github.com/viadee/vPW/pull/37)
* Fix docs 

### Updates:
- [Bump maven-site-plugin from 3.9.1 to 3.11.0](https://github.com/viadee/vPW/pull/40)
- [Bump postgresql from 42.3.0 to 42.3.3](https://github.com/viadee/vPW/pull/39)
- [Bump maven-project-info-reports-plugin from 3.1.2 to 3.2.1](https://github.com/viadee/vPW/pull/35)
- [Bump hibernate-validator from 7.0.1.Final to 7.0.2.Final](https://github.com/viadee/vPW/pull/30)
- [Bump spring-aspects from 5.3.12 to 5.3.15](https://github.com/viadee/vPW/pull/25)
- [Bump postgres image from postgres:10.2-alpine to postgres:14-alpine in docker-compose.yml and docker-compose_developer.yml](https://github.com/viadee/vPW/commit/785564715c44e5bd8712571ab7c73fbab7450878)

-------------------------------------------

## 1.0.2
Fix Release.

#### Version Look Up

| Typ                     | Name                     | Version | Source                                                       |
| ----------------------- | ------------------------ | ------- | ------------------------------------------------------------ |
| Docker image            | vpw-analyzer             | 7.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-pipeline             | 7.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-frontend             | 7.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-polling-client       | 2.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Helm chart              | vpw-analyzer-chart       | 1.0.0   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-pipeline-chart       | 1.0.0   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-frontend-chart       | 1.0.1   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-polling-client-chart | 1.0.1   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart (umbrella)   | vpw-chart                | 1.0.2   | [viadee charts repository](https://github.com/viadee/charts) |


### Features
/
### Fixes
* Aligning values of vpw-polling-client subchart with values in umbrella chart

-------------------------------------------

## 1.0.1
Fix Release.

#### Version Look Up

| Typ                     | Name                     | Version | Source                                                       |
| ----------------------- | ------------------------ | ------- | ------------------------------------------------------------ |
| Docker image            | vpw-analyzer             | 7.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-pipeline             | 7.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-frontend             | 7.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-polling-client       | 2.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Helm chart              | vpw-analyzer-chart       | 1.0.0   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-pipeline-chart       | 1.0.0   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-frontend-chart       | 1.0.1   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-polling-client-chart | 1.0.0   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart (umbrella)   | vpw-chart                | 1.0.1   | [viadee charts repository](https://github.com/viadee/charts) |


### Features
/
### Fixes
* use .Chart.AppVersion as default image tag version in vpw-frontend-deployment.yaml

-------------------------------------------

## 1.0.0
First open source release.

#### Version Look Up

| Typ                     | Name                     | Version | Source                                                       |
| ----------------------- | ------------------------ | ------- | ------------------------------------------------------------ |
| Docker image            | vpw-analyzer             | 7.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-pipeline             | 7.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-frontend             | 7.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Docker image            | vpw-polling-client       | 2.0.0   | [AWS ECR](https://gallery.ecr.aws/viadee/)                   |
| Helm chart              | vpw-analyzer-chart       | 1.0.0   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-pipeline-chart       | 1.0.0   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-frontend-chart       | 1.0.0   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart              | vpw-polling-client-chart | 1.0.0   | [viadee charts repository](https://github.com/viadee/charts) |
| Helm chart (umbrella)   | vpw-chart                | 1.0.0   | [viadee charts repository](https://github.com/viadee/charts) |


### Features
/
### Fixes
/
