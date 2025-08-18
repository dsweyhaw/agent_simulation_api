FROM gradle:8.8-jdk21

WORKDIR /app

COPY . .

COPY /docker/local/api/docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

COPY /gama/gama-platform /opt/gama-platform
RUN chmod -R 777 /opt/gama-platform

CMD ["/docker-entrypoint.sh"]
