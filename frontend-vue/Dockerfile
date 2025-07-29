# Étape 1 : Build du frontend avec npm
FROM node:20 AS build
WORKDIR /app
COPY . .
RUN npm install && npm run build

# Étape 2 : Servir les fichiers avec NGINX
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]