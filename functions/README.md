# Google Cloud functions

***IMPORTANT***
---------------

> After every addition of a new function in ***/functions*** folder,
> update ***cloud-functions.yaml*** in the following manner:

1. Create a new job with a name ---> **[name]_deploy** in the first line

2. Change appropriate information in regard to the new function (set additional **env-variables** if needed)

```yaml
  search_movie_function_deploy: 
    runs-on: ubuntu-latest
    steps:
      - name: checkout repository
        uses: actions/checkout@v3

      - name: Authenticate to Google Cloud
        uses: google-github-actions/auth@v1.1.1
        with:
          project_id: ${{env.PROJECT}}
          credentials_json: ${{secrets.GCLOUD_AUTH}}

      - name: Set up Cloud SDK
        uses: google-github-actions/setup-gcloud@v1.1.1

      - name: Deploy Cloud Search Function <--- change name
        run: |
          gcloud functions deploy search-movie-func \  <--- change name
            --entry-point searchmovie.SearchMovie \  <--- change entry-point
            --source=./functions/search-movie-func \  <--- change source
            --runtime java17 \
            --trigger-http \
            --region europe-north1 \
            --project ${{ secrets.GC_PROJECT_ID }} \
            --gen2 \
            --set-env-vars TMDB_API_KEY=${{ secrets.TMDB_API_KEY }}  <--- set needed env-variables
```
<hr>

> After every addition of a new function in ***/functions*** folder,
> update ***function-build.yaml*** in the following manner:

1. Change to appropriate job name in the first line ---> **build_[name]:**
2. Find path to your new function and place it instead of ellipsis ***run: cd ... &&***
in each ***run*** command

```yaml
  build_get_cast_and_trailer_func:
    runs-on: ubuntu-latest
    env:   <--- add any env variables if needed (or omit this section if none are required)
      NEO4J_URI: ${{ secrets.NEO4J_URI }}
      NEO4J_USER: ${{ secrets.NEO4J_USER }}
      NEO4J_PASSWORD: ${{ secrets.NEO4J_PASSWORD }}
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      - name: Run the Maven verify, build and test
        run: cd functions/get-cast-and-trailer-func && mvn --batch-mode --update-snapshots verify  <--- change path
```