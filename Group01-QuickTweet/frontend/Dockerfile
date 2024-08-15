# Description: Production Dockerfile for the frontend service

FROM node:lts as build 

WORKDIR /app

COPY package*.json ./

COPY . .

RUN npm i 

RUN npm run build

FROM nginx:alpine

WORKDIR /usr/share/nginx/html

RUN rm -rf ./*

COPY --from=build /app/dist .

ENTRYPOINT ["nginx", "-g", "daemon off;"]