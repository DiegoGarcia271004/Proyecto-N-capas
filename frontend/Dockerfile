# ── Stage 1: build ──────────────────────────────────────────────────────────
FROM node:20-alpine AS builder
WORKDIR /app

ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

COPY package*.json .
RUN npm i --silent

COPY . .
RUN npm run build

# ── Stage 2: runtime con nginx ───────────────────────────────────────────────
FROM nginx:1.27-alpine
WORKDIR /usr/share/nginx/html

# Eliminar config por defecto de nginx
RUN rm -rf ./*

COPY --from=builder /app/dist .

# La config de nginx se inyecta desde fuera vía volumen/bind en docker-compose
# para poder usar la misma imagen en dev y prod con distinta config

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]