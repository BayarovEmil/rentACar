# Build aşaması
FROM node:20 as build-stage

WORKDIR /app

# package.json ve package-lock.json dosyalarını kopyalayın
COPY rent-a-car-ui/rent-a-car-ui/package*.json ./

# NPM bağımlılıklarını yükleyin
RUN npm install

# Proje dosyalarını kopyalayın
COPY rent-a-car-ui/rent-a-car-ui .

# Angular projesini derleyin
RUN npm run build -- --configuration production

# Sunucu aşaması
FROM nginx:alpine

# Nginx yapılandırma dosyasını kopyalayın
COPY rent-a-car-ui/nginx.conf /etc/nginx/nginx.conf

# Derlenen dosyaları Nginx dizinine kopyalayın
COPY --from=build-stage /app/dist/rent-a-car-ui /usr/share/nginx/html

# 80 portunu expose edin
EXPOSE 80

# Nginx'i çalıştırın
CMD ["nginx", "-g", "daemon off;"]
