---
title: Getting Started with Helm
parent: Getting Started
nav_order: 2
---
# Getting Started with Helm

## 1. Usage

* [Helm](https://helm.sh) must be installed to use the charts. Please refer to
  Helm's [documentation](https://helm.sh/docs) to get started.

* Once Helm has been set up correctly, add the repo as follows:

  `helm repo add viadee https://viadee.github.io/charts`

* If you had already added this repo earlier, run `helm repo update` to retrieve
  the latest versions of the packages.  You can then run `helm search repo
  viadee` to see the charts.

* To install the <chart-name> chart:

  `helm install my-<chart-name> viadee/<chart-name>`

* To uninstall the chart:

  `helm delete my-<chart-name>`


## 2. Available Charts 

The following charts are available: 

* [vpw-analyzer-chart](https://github.com/viadee/vPW/tree/main/deployment/helm) 
* [vpw-pipeline-chart](https://github.com/viadee/vPW/tree/main/deployment/helm)
* [vpw-frontend](https://github.com/viadee/vPW/blob/main/deployment/helm-umbrella/vpw-chart/values.yaml) - values of vpw-frontend can be found in vpw-chart. 
* [vpw-polling-client-chart](https://github.com/viadee/camunda-kafka-polling-client)  
* [vpw-chart](https://github.com/viadee/vPW/tree/main/deployment/helm-umbrella/vpw-chart)

It is recommended to install the *vpw-chart*, since as an umbrella chart it combines all vpw components (vpw-analyzer-chart, vpw-pipeline-chart, vpw-frontend-chart, vpw-polling-client) and therefore allows an easy deployment.


## 3. Configuration

The umbrella chart only contains the vpw components (vpw-analyzer, vpw-pipeline, vpw-frontend, vpw-polling-client). Therefore, you also need to deploy the following components on your own: 
* *process engine* from which the polling client can poll process information.
* *kafka with zookeeper* to stream the process information from which the pipeline polls the process information.
* *elasticsearch* to which the pipeline and the analyzer can connect.
* *database* to which the analyzer can connect. 

### 3.1 Process engine with polling client: 
* If you do not have a deployed instance of a process engine in your kubernetes cluster running, you need to deploy one: 
    * We recommend using the following helm chart to deploy the [Camunda BPM Platform](https://github.com/camunda-community-hub/camunda-helm/tree/main/charts/camunda-bpm-platform#camunda-bpm-platform-helm-chart).
* Adjust the Values.yaml for the umbrella helm chart in the polling client section accordingly to the used process engine (here [Camunda BPM Platform](https://github.com/camunda-community-hub/camunda-helm/tree/main/charts/camunda-bpm-platform#camunda-bpm-platform-helm-chart)):
  
  `vpw-polling-client.environment.secret.camundaPassword: "demo"`
  
  `vpw-polling-client.environment.configMap.camundaRestUrl: http://camunda-bpm-platform:8080/engine-rest/`
  
  `vpw-polling-client.environment.configMap.username: "demo"`

### 3.2 Kafka with polling client and pipeline:
* If you do not have an apache kafka instance deployed, you need to deploy one: 
    * We would recommend using the kafka strimzi operator to manage your kafka deployment: [strimzi.io](https://strimzi.io/)
    * See __kafka cluster configuration documentation__ here: [documentation](https://strimzi.io/docs/operators/latest/overview.html#configuration-points-broker_str) & [custom resource definition files](https://github.com/strimzi/strimzi-kafka-operator/tree/main/examples/kafka)
* Adjust the Values.yaml for the umbrella chart in the polling client and pipeline section via connecting to the exposed service of kafka:
  
    `vpw-polling-client.environment.configMap.kafkaBootstrapServers: http://kafka-zookeeper-kafka-bootstrap:9092` 
  
    `vpw-pipeline.environment.configMap.kafka_bootstrap_servers: http://kafka-zookeeper-kafka-bootstrap:9192`
    
    * ___Notice 0:___ You can find the custom resource definition used for our deployment in our [charts repository](https://github.com/viadee/charts/tree/main/helper-charts).
    * ___Notice 1:___ The Kafka address depends on the selected Kafka configuration and may therefore differ from the address shown here.
    * ___Notice 2:___ The Kafka port differs for polling client and pipeline, since the polling client puts information on the kafka stream, and the pipeline consumes them. Refer to the kafka documentation for detailed information.

### 3.3 Elasticsearch with pipeline and analyzer:
* If you do not have an elasticsearch instance running in you cluster, you need to deploy one.
* You could use the following helm chart [elastic chart](https://github.com/elastic/helm-charts)
* Alternatively, you can use the [Elasticsearch operator](https://www.elastic.co/guide/en/cloud-on-k8s/current/k8s-deploy-eck.html). This procedure is also our recommendation and is briefly described below.
  * After deploying the operator, apply the [custom resource definition](https://www.elastic.co/guide/en/cloud-on-k8s/current/k8s-deploy-elasticsearch.html) to create an elasticsearch cluster. 
  * You can find the custom resource definition used for our deployment in our [charts repository](https://github.com/viadee/charts/tree/main/helper-charts).
  * Adjust the Values.yaml for the umbrella chart in the pipeline and analyzer section to connect to the elasticsearch instance:
      
      `vpw-pipeline.environment.configMap.spring_elasticsearch_rest_uris: "http://elasticsearch-es-http:9200"`
      
      `vpw-analyzer.environment.configMap.spring_elasticsearch_rest_uris: "http://elasticsearch-es-http:9200"`

    * ___Notice 0:___ You also need to set the user, given by the elasticsearch instance (or create a new user), for the [analyzer](https://github.com/viadee/charts/blob/main/charts/vpw-chart/values.yaml#L125) and [pipeline](https://github.com/viadee/charts/blob/main/charts/vpw-chart/values.yaml#L270) as plaintext or by referring to an external secret.  
    * ___Notice 1:___ The elastic address depends on the selected elastic configuration and may therefore differ from the address shown here.
  

### 3.4 Database with analyzer
* The analyzer requires a database to connect, e.g. postgresql.
* Here it is also recommended using an [operator](https://postgres-operator.readthedocs.io/en/latest/) to deploy a database instance.
* Our custom resource definition used for our deployment can be found in our [charts repository](https://github.com/viadee/charts/tree/main/helper-charts).   
* Adjust the Values.yaml for the umbrella chart in the analyzer section accordingly to your database settings:
  
    `vpw-analyzer.environment.secret.postgresql_pwd: "vpw"`
  
    `vpw-analyzer.environment.configMap.postgresql_host: "analyzer-postgres-postgresql"`
  
    `vpw-analyzer.environment.configMap.postgresql_port: "5433"`
  
    `vpw-analyzer.environment.configMap.postgresql_databasename: "vpw"`
  
    `vpw-analyzer.environment.configMap.postgresql_user: "vpw"`

### 3.5 Ingress Configuration and Basic Authentication
* By default, ingress is enabled with some placeholder values (`vpw-frontend.ingress.`). You need to insert your domain and tls information so that the frontend is accessible from outside the cluster. 
* Alternatively, you can "port-forward" the service port of the frontend to your localhost. 
* By default, the nginx of the frontend is secured via basic authentication (*demo:demo*). This can be changed and is explained [here](https://github.com/viadee/charts/blob/main/charts/vpw-frontend-chart/values.yaml#L17).

## 4. Deploy Script
We provide a (static) [script](https://github.com/viadee/vPW/tree/main/deployment/deploy_script) to deploy all components together in a kubernetes cluster. Please note, that this script & resulting deployment is not suitable for production environments as it serves only for testing and developing.
When running the script, the namespace that will be created in the cluster must be specified via the command line. Please note, that we recommend to use the namespace "vpw", because some connection names assume that the namespace is called in that way (see e.g. .kafka_bootstrap_servers). You also need to be on the same folder level as the "kafka-ephemeral-single.yaml" and "postgresql-secret.yaml" files.  

`sh .\k8_deploy_vpw_withComponents_script.sh "vpw"`
