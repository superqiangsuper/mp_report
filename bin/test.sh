#!/usr/bin/env bash

curl -X DELETE http://localhost:8090/contexts/context_mp_report
curl -d "" 'localhost:8090/contexts/context_mp_report?context-factory=spark.jobserver.context.DefaultSparkContextFactory'
curl --data-binary @/Users/admin/IdeaProjects/mp-report/mp_report/target/mp-report-1.0-SNAPSHOT-jar-with-dependencies.jar localhost:8090/jars/mp_report

# curl --data-binary @/Users/admin/Downloads/mp.json "localhost:8090/jobs?appName=mp_report&classPath=com.jfbank.data.MpTrackJob"

