# 构建阶段
FROM node:22 AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build

# 运行阶段（使用 Nginx 提供前端服务）
FROM nginx:alpine
COPY --from=build /app/dist /app/dist
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
