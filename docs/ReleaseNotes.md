---
title: Release Notes
nav_order: 3
---
# Release notes

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
