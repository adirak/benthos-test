# Recources
processor_resources:
  - label: "my_resource"
    hp_http:
      url: "${!base_url}${!path}"
      verb: GET

      input: |
        root = this

      result_mode: "new"

      output: |
        root = {}
        root.result = this

      timeout: 5s
