## 1. Setup
- Chạy với quyền cao nhất
    ```
    sudo su
    ```
- Update package manager
    ```
    apt update
    apt-get update
    ```
- Tạo thư mục '/var/www/html'
    ```
    mkdir -p /var/www/html
    ```
- Clone source code project master 'https://github.com/dttrung257/agent_simulation_api'
    ```
    cd /var/www/html
    git clone https://github.com/dttrung257/agent_simulation_api
    cd /var/www/html/agent_simulation_api
    git checkout develop
    ```
- Install GAMA
    ```
    cd /var/www/html/agent_simulation_api/gama
    apt update
    apt install wget unzip
    wget https://github.com/gama-platform/gama/releases/download/1.9.3/GAMA_1.9.3_Linux.zip
    unzip GAMA_1.9.3_Linux.zip -d gama-platform
    rm −f GAMA_1.9.3_Linux.zip
    ```
- Install Docker
    ```
    for pkg in docker.io docker-doc docker-compose docker-compose-v2 podman-docker containerd runc; do sudo apt-get remove $pkg; done
    ```
    
    ```
    # Add Docker's official GPG key:
    sudo apt-get update
    sudo apt-get install ca-certificates curl
    sudo install -m 0755 -d /etc/apt/keyrings
    sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
    sudo chmod a+r /etc/apt/keyrings/docker.asc

    # Add the repository to Apt sources:
    echo \
    "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
    $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
    sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update
    ```

    ```
    sudo apt install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    ```

    ```
    sudo groupadd docker
    sudo usermod -aG docker $USER
    newgrp docker
    ```

    ```
    docker --version
    ```
- Deploy
    ```
    cd /var/www/html/agent_simulation_api
    git fetch origin develop; git reset --hard origin/develop
    docker compose up --build -d; docker logs -f ags_dev_api
    ```

- Clean after deploy
    ```
    docker system prune -f
    ```
- Cách đổi IP config cho cluster
    - Sửa IP trong file cluster-config
    ```
    vi /var/www/html/agent_simulation_api/src/main/resources/cluster-config.yml
    ```
    - Sau đó restart lại container api
    ```
    docker compose restart api; docker logs -f ags_dev_api
    ```
    - Sửa .env của frontend
    ```
    vi /var/www/html/agent_simulation_frontend/.env
    ```
    - Sau khi sửa IP
    ```
    cd /var/www/html/agent_simulation_frontend
    npm run build
    ```
    - Reload nginx
    ```
    rm -rf /var/www/html/build
    mv build /var/www/html/
    nginx -s reload
    ```
    - Sửa IP ở project agent_simulation_worker: ssh và các con worker sau đó vào thư mục /var/www/html/agent_simulation_worker. Sửa file .env 2 trường SPRING_DATASOURCE_UR và SPRING_DATA_REDIS_HOST bằng IP Node Master mới.
    - Rebuild project worker
- Truy cập database DEV từ console (Đã ssh)
    ```
    docker exec -it ags_dev_db mysql -u agsuser ags -pmysql123456
    ```
- Restart API container
    ```
    docker compose restart api; docker logs -f ags_dev_api
    ```
- Tạo lại API container
    ```
    docker compose down api; docker compose up api -d; docker logs -f ags_dev_api
    ```

## 2. Frontend
- Clone source code project frontend:
```
    cd /var/www/html
    git clone https://github.com/dttrung257/agent_simulation_frontend
    cd /var/www/html/agent_simulation_frontend
    git checkout develop
```

Install nvm:
```
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.3/install.sh | bash

```

```
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"  # This loads nvm bash_completion
```

```
nvm install 20
```

```
nvm use v20
```

Create .env file:
```
vi /var/www/html/agent_simulation_frontend/.env
```

```
REACT_APP_API_URL=http://<ip_master>/api/v1
# REACT_APP_API_KEY=eyJhbGciOiJIUzUxMiJ9...
```

Build:
```
npm install
npm run build
```

- Move build to var/www/html:
```
mv /var/www/html/agent_simulation_frontend/build /var/www/html/
```


Nginx
Create config file
```
vi /etc/nginx/sites-available/ags
```

- Nginx
```
upstream api_group {
    server localhost:8080;
}

server {
    listen 80;
    listen [::]:80;
    server_name localhost;

    root /var/www/html/build;

    location /api/ {
        proxy_pass http://api_group;
        proxy_redirect off;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header Host $http_host;
        proxy_set_header X-NginX-Proxy true;
        client_max_body_size 11M;
    }

    location / {
        try_files $uri $uri/ /index.html =404;
    }
}
```

```
cd /etc/nginx/sites-enabled
rm default
```

```
ln -s /etc/nginx/sites-available/ags /etc/nginx/sites-enabled/ags
```

```
nginx -t
```

```
nginx -s reload
```

Run script to insert data to database:
```
chmod +x scripts/import_project.sh
./scripts/import_project.sh
```
