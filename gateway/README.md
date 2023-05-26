# Gateway API specifications

***IMPORTANT***
---------------

> After every change in ***open-api.yaml***,
> update ***gateway.yaml*** in the following manner:

1. Increment the current **api-config** version number in here ---> 
   **api-config-v3**

2. Copy the new version of **api-config** to **Deploy API to Cloud API Gateway**
   job and paste in place of ellipsis ---> **'api-configs create ...'**

```yaml
      - name: Deploy API to Cloud API Gateway
        run: |
           gcloud api-gateway api-configs create api-config-v3 \ <---
             --api=api-id \
             --project=${{ secrets.GC_PROJECT_ID }} \
             --openapi-spec=./gateway/open-api.yaml
```

3. Adjust a version of a new **api-config** in the **Update API Gateway** job
   under ---> **'--api-config='**

```yaml
      - name: Update API Gateway
        run: |
           gcloud api-gateway gateways update movies-gateway \
             --api=api-id \
             --api-config=api-config-v3 \ <---
             --location=europe-west1	\
             --project=${{ secrets.GC_PROJECT_ID }}
```