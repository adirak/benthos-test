-- custom-response-modifier/handler.lua
local BasePlugin = require "kong.plugins.base_plugin"
local http = require "resty.http"
local cjson = require "cjson"

local CustomResponseModifier = BasePlugin:extend()

CustomResponseModifier.PRIORITY = 800
CustomResponseModifier.VERSION = "1.0.0"

function CustomResponseModifier:new()
  CustomResponseModifier.super.new(self, "custom-response-modifier")
end

function CustomResponseModifier:access(conf)
  CustomResponseModifier.super.access(self)
  -- เก็บ start time สำหรับ log
  kong.ctx.plugin.start_time = ngx.now()
end

function CustomResponseModifier:header_filter(conf)
  CustomResponseModifier.super.header_filter(self)
  -- ตั้งค่า header เพื่อให้สามารถ modify response body ได้
  ngx.header.content_length = nil
  -- เปิดการใช้งาน response modification
  kong.response.clear_header('Content-Length')
end

function CustomResponseModifier:body_filter(conf)
  CustomResponseModifier.super.body_filter(self)
  
  local chunk, eof = ngx.arg[1], ngx.arg[2]
  
  -- เก็บ response body เดิม
  if not kong.ctx.plugin.response_body then
    kong.ctx.plugin.response_body = ""
  end
  
  if chunk then
    kong.ctx.plugin.response_body = kong.ctx.plugin.response_body .. chunk
  end

  -- ถ้ายังไม่จบ response ให้ clear chunk ก่อน
  if not eof then
    ngx.arg[1] = nil
    return
  end

  -- เมื่อได้ response body ทั้งหมดแล้ว
  if eof then
    -- แปลง response เป็น JSON
    local original_response = cjson.decode(kong.ctx.plugin.response_body)
    
    -- เรียก external API
    local httpc = http.new()
    local res, err = httpc:request_uri(conf.external_api_url, {
      method = "GET",
      headers = {
        ["Content-Type"] = "application/json",
      }
    })
    
    if not res then
      kong.log.err("Failed to call external API: ", err)
      return
    end
    
    -- แปลง response จาก external API เป็น JSON
    local external_data = cjson.decode(res.body)
    
    -- รวม data จาก original response และ external API
    original_response.external_data = external_data
    
    -- แปลงกลับเป็น JSON string
    local modified_body = cjson.encode(original_response)
    
    -- ส่ง modified response กลับไป
    ngx.arg[1] = modified_body
    ngx.arg[2] = true
  end
end

function CustomResponseModifier:log(conf)
  CustomResponseModifier.super.log(self)
  -- คำนวณเวลาที่ใช้
  local time_taken = ngx.now() - kong.ctx.plugin.start_time
  kong.log.debug("Time taken by custom-response-modifier: ", time_taken, " seconds")
end

return CustomResponseModifier