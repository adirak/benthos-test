-- custom-response-modifier/schema.lua
local typedefs = require "kong.db.schema.typedefs"

return {
  name = "custom-response-modifier",
  fields = {
    { consumer = typedefs.no_consumer },
    { protocols = typedefs.protocols_http },
    { config = {
        type = "record",
        fields = {
          { external_api_url = { type = "string", required = true } },
        },
      },
    },
  },
}