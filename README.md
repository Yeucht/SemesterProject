# Scalable IoT Data Benchmarking Platform

This project provides a horizontally scalable simulation and benchmarking platform for high-throughput IoT data injection.  
It integrates multiple NoSQL databases (QuestDB, Apache IoTDB) and a relational store, with full observability through Prometheus, Grafana, and Cadvisor.  
A Spring Boot backend coordinates the ingestion, while a Vue.js dashboard allows interactive configuration, monitoring, and a simple invoicing example.

---

## Table of Contents
- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [1. Enable WSL2 (Windows only)](#1-enable-wsl2-windows-only)
  - [2. Install Docker Desktop](#2-install-docker-desktop)
  - [3. Verify Installation](#3-verify-installation)
  - [4. Configure Metrics](#4-configure-metrics)
- [Launching the Containers](#launching-the-containers)
  - [Web Service (Spring Boot 3.2.0, JDK17)](#web-service-spring-boot-320-jdk17)
  - [Web Dashboard (Vue.js, Node.js 20)](#web-dashboard-vuejs-nodejs-20)
  - [Simulation Module (Flask)](#simulation-module-flask)
  - [QuestDB (v902)](#questdb-v902)
  - [Apache IoTDB (v132 Standalone)](#apache-iotdb-v132-standalone)
  - [MySQL Workbench (Optional)](#mysql-workbench-optional)
- [Usage](#usage)
- [Architecture](#architecture)
  - [Java Backend](#java-backend)
  - [Frontend (Vue.js)](#frontend-vuejs)
  - [Databases](#databases)
- [Extending the Platform](#extending-the-platform)
- [License](#license)

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
Restart your PC

Install the WSL2 kernel

2. Install Docker Desktop

Download Docker Desktop 4.43.2

Run the installer and check “Use WSL2”

Finish the installation

3. Verify Installation
