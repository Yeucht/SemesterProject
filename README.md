# Scalable IoT Data Benchmarking Tool

This project provides a horizontally scalable simulation and benchmarking platform for high-throughput IoT data injection.  
It integrates multiple NoSQL databases (QuestDB, Apache IoTDB) and a relational store, with full observability through Prometheus, Grafana, and Cadvisor.  
A Spring Boot backend coordinates the ingestion, while a Vue.js dashboard allows interactive configuration, monitoring, and a simple invoicing example.

---

## Table of Contents
- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [1. Activate the VM](#1-activate-the-vm)
  - [2. Install Docker Desktop](#2-install-docker-desktop)
  - [3. Launching the containers](#3-launching-the-containers)
    - [3.1 Web Service (Spring Boot 3.2.0, JDK17) and Performance Tracking](#31-web-service-spring-boot-320-jdk17-and-performance-tracking-prometheus-cadvisor-grafana--all-latest)
    - [3.2 Web Dashboard (Node.js:20-Vue.js)](#32-web-dashboard-nodejs20-vuejs)
    - [3.3 Simulation Module (Flask 3.1.1 on Gunicorn 23.0.0)](#33-simulation-module-flask-311-on-gunicorn-2300)
    - [3.4 QuestDB (v9.0.2)](#34-questdb-v902)
    - [3.5 IoTDB (v1.3.2 Standalone)](#35-iotdb-v132-standalone)
    - [3.6 MySQL 8.4](#36-mysql-84)
    - [3.7 Workbench MySQL (8.0.40) (optional)](#37-workbench-mysql-8040-optional)
- [Usage](#usage)
  - [Open the dashboard](#open-the-dashboard-at-httplocalhost7000)
  - [Simulations generate](#simulations-generate)
  - [The Invoicing page allows](#the-invoicing-page-allows)
  - [Results storage](#results-are-stored-in-a-relational-database-the-mysql-one-with)
- [Adding new material](#adding-new-material)
  - [Adding a new DataBase](#adding-a-new-database)
  - [Adding a new feature](#adding-a-new-feature)


---

## Installation

### Prerequisites
- [**Docker Desktop 4.43.2+**](https://docs.docker.com/desktop/release-notes/) (Windows 10 64-bit or later, or any Docker-enabled Unix VM)
- **WSL2** enabled on Windows
- Optional: [**MySQL Workbench 8.0.40**](https://dev.mysql.com/downloads/workbench/)

### 1. Activate the VM
### 1.1 Enable WSL2 (Windows only)
```powershell
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
```
1.2 Restart your PC

1.3 Install the WSL2 kernel

### 2. Install Docker Desktop

2.1 [**Download Docker Desktop 4.43.2**](https://docs.docker.com/desktop/release-notes/?utm_source=chatgpt.com#4432)

2.2 Run the installer and check “Use WSL2”

2.3 Finish the installation

2.4 Verify Installation
```powershell
docker --version
docker run hello-world
```
-> If you see <i>Hello from Docker!</i>, you're fine. 

2.5 Modify settings
Go to Settings > docker engine
Modify the json with:
```code
{
  "builder": {
    "gc": {
      "defaultKeepStorage": "20GB",
      "enabled": true
  }
  },
  "experimental": false,
  "metrics-addr": "127.0.0.1:9323"
}
```

### 3. Launching the containers

#### 3.1 Web Service (Spring Boot 3.2.0, JDK17) and Performance Tracking (Prometheus, Cadvisor, Grafana -all latest)

3.1.1 Download this whole git repository and place it at <i>path</i>

3.1.2 In powershell, run
```powershell
cd path/service
docker build -t sp-service .
docker compose up --build
```
Note: The web service exposes on port 8080, and you can find all ports used on docker desktop.

#### 3.2 Web Dashboard (Node.js:20-Vue.js)

3.2.1 Go to <i>path</i>

3.2.2 Build and run
```powershell
cd frontend-vue
docker build -t sp-frontend .
docker run --network sp-net -p 7000:80 --name sp-frontend sp-frontend
```
Note: The web dashboard exposes on port 7000. You can access it on [**localhost:7000**](http://localhost:7000) in a browser. If you have issues, it might be your firewall, that you can disable for this port especially, but don't allow incoming traffic. 

#### 3.3 Simulation Module (Flask 3.1.1 on Gunicorn 23.0.0)

3.3.1 [**Download**](https://github.com/Yeucht/SemesterProjectSimulation) and place the repo files at <i>path</i>

3.3.2 Build and run
```powershell
cd path/simulation
docker build -t sp-simulation .
docker run -d --network sp-net -p 8000:8000 --name sp-simulation sp-simulation
```

#### 3.4 QuestDB (v9.0.2)

```powershell
docker run -d --name questdb --network sp-net \
  -p 9000:9000 -p 9009:9009 -p 8812:8812 \
  -e QDB_METRICS_ENABLED=true \
  -e QDB_CAIRO_WAL_ENABLED_DEFAULT=true \
  -e QDB_LINE_AUTO_CREATE_NEW_TABLES=true \
  questdb/questdb:9.0.2
```

#### 3.5 IoTDB (v1.3.2 Standalone)

3.5.1 Prepare configuration (again choose a <i>path</i>)
```powershell
mkdir -p path/iotdb
docker run --name iotdb-temp -d apache/iotdb:1.3.2-standalone
docker cp iotdb-temp:/iotdb/conf path/iotdb
docker rm -f iotdb-temp
```
Note: Here we just pull the volumes we need to change to configure and remove the rest.

3.5.2 Edit path/iotdb/conf/iotdb-datanode.properties: 
```code
enable_auto_create_schema=true
default_storage_group_level=1
dn_metric_reporter_list=PROMETHEUS
dn_metric_level=IMPORTANT
dn_metric_prometheus_reporter_port=9091
```

3.5.3 Run IoTDB
```powershell
docker run -d --name iotdb --network sp-net -p 6667:6667 -p 8181:8181 -p 5555:5555 -p 9091:9091 -v path/iotdb/data:/iotdb/data -v path/iotdb/logs:/iotdb/logs -v path/iotdb/conf/conf:/iotdb/conf apache/iotdb:1.3.2-standalone
```
#### 3.6 MySQL 8.4 

[**Download**](https://dev.mysql.com/downloads/mysql/8.4.html) and Install MySQL 8.4.6 Server LTS

#### 3.7 Workbench MySQL (8.0.40) (optional)
Workbench works as a DataBase visualizer, so it is optional in the sense that you can use another tool.

3.7.1 [**Download**](https://dev.mysql.com/downloads/workbench/) and Install Workbench

3.7.2 Open a new connection
```
Host: localhost
Port: 3306
User: root
Password: root123
```

## Usage

### Open the dashboard at http://localhost:7000

<img width="1119" height="701" alt="image" src="https://github.com/user-attachments/assets/3982397b-0f27-4562-92bc-90cbc678127d" />

Configure simulation parameters (HES count, smart meters, send rates, randomness)

Start/stop simulations and monitor system activity

### Simulations generate:

IoT data ingestion into QuestDB and IoTDB

Performance metrics collected by Prometheus

Dashboards automatically provisioned in Grafana

<img width="1114" height="778" alt="image" src="https://github.com/user-attachments/assets/b2981878-c992-420d-8775-7760199af3a3" />

### The Invoicing page allows:

Selection of time window and kWh price

Meter-based billing example

<img width="1139" height="217" alt="image" src="https://github.com/user-attachments/assets/385806b9-82c5-48ec-b5c8-bd25994f503c" />

### Results are stored in a relational database (the MySQL one) with:

simulation_run → metadata for runs

simulation_config → configuration used

metric_point → captured metrics


## Adding new material

Here are the main and least actions to take for helping enhancing the project.

### Adding a new DataBase

Implement a DBManager subclass with:
```
clearTables()
getRowCount()
getNumberMeters()
```
Add ingestion/extraction classes

Expose metrics to Prometheus

### Adding a new feature

Extend controllers and services in the backend

Create new Vue components for the frontend

Add Grafana dashboards if needed
