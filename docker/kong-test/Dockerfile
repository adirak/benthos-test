# Dockerfile
FROM kong:3.5

USER root

# ติดตั้ง dependencies ที่จำเป็น
RUN apt-get update && \
    apt-get install -y git unzip luarocks

# สร้างโฟลเดอร์สำหรับ custom plugins
RUN mkdir -p /usr/local/kong/plugins/custom-response-modifier

# คัดลอก plugin files
COPY kong-plugins/custom-response-modifier /usr/local/kong/plugins/custom-response-modifier/

# ติดตั้ง plugin
RUN cd /usr/local/kong/plugins/custom-response-modifier && \
    luarocks make

# แก้ไขสิทธิ์
RUN chown -R kong:kong /usr/local/kong/plugins

USER kong