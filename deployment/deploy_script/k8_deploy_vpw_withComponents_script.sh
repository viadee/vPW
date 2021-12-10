#!/bin/bash

# create vpw namespace
echo "## Create vpw namespace [0/5]"
kubectl create namespace vpw


# camunda bpm platform
echo "## Deploy camunda bpm plattform [1/5]"

helm repo add camunda https://helm.camunda.cloud
helm repo update
helm install camunda-webapp camunda/camunda-bpm-platform -n vpw

sleep 10



# kafka + zookeeper
echo "## Deploy kafka with stimizi-kafka-operator [2/5]"

helm repo add strimzi https://strimzi.io/charts/
helm repo update
helm install strimzi-kafka-operator strimzi/strimzi-kafka-operator -n vpw
sleep 10
kubectl apply -f kafka-ephemeral-single.yaml -n vpw

sleep 10



# elasticsearch
echo "## Deploy elasticsearch [3/5]"
helm repo add elastic https://helm.elastic.co
helm repo update
helm install --set "clusterName=vpw" --set imageTag=7.13.4 --set resources.requests.cpu=100m --set resources.requests.memory=512M --set resources.limits.cpu=1000m --set resources.limits.memory=512M  --set volumeClaimTemplate.resources.requests.storage=1Gi --set "nodeGroup=master" --set "replicas=1" --set "minimumMasterNodes=1" --set extraEnvs[0].name=ELASTICSEARCH_USERNAME,extraEnvs[0].value=elastic  --set extraEnvs[1].name=ELASTICSEARCH_PASSWORD,extraEnvs[1].value=elastic elasticsearch elastic/elasticsearch -n vpw

sleep 10



# postgresql
echo "## Deploy postgresql [4/5]" 
helm repo add bitnami https://charts.bitnami.com/bitnami
kubectl apply -f postgresql-secret.yaml -n vpw
helm install --set  global.postgresql.postgresqlDatabase=vpw --set global.postgresql.postgresqlUsername=vpw --set global.postgresql.existingSecret=postgresql-secret --set global.postgresql.postgresqlPassword="" --set global.postgresql.servicePort=5433 analyzer-postgres bitnami/postgresql -n vpw

sleep 10



# vPW
echo "Deploy vPW [5/5]"
helm repo add viadee https://viadee.github.io/charts
helm repo update
helm install vpw viadee/vpw-chart -n vpw

echo "#### All components are deployed. Please note that some components may take several minutes to start. ####"
echo "#### You could port-forward the vpw-frontend to your localhost:8080 with the command 'kubectl port-forward service/vpw-frontend 8080:8080 -n vpw'  ####"