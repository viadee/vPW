{{/*
Expand the name of the chart.
*/}}

{{- define "vpw-analyzer-chart.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}

{{- define "vpw-analyzer-chart.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}

{{- define "vpw-analyzer-chart.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}

{{- define "vpw-analyzer-chart.labels" -}}
helm.sh/chart: {{ include "vpw-analyzer-chart.chart" . }}
{{ include "vpw-analyzer-chart.selectorLabels" . }}
{{- if .Chart.AppVersion }}
appVersion: {{ .Chart.AppVersion | quote }}
{{- end }}
managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}

{{- define "vpw-analyzer-chart.selectorLabels" -}}
name: {{ include "vpw-analyzer-chart.name" . }}
app: {{ .Values.selectorLabels.app | quote }}
component: {{ .Values.selectorLabels.component | quote }}
release: {{ .Values.selectorLabels.release | quote }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}

{{- define "vpw-analyzer-chart.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "vpw-analyzer-chart.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}
