# WebScraper (REST API Aggregator)

A console-based Java application for aggregating data from multiple REST APIs with support for parallel processing and result persistence.

---

## 📌 Description

The application allows you to:
- fetch data from multiple REST APIs;
- convert JSON responses into Java objects;
- aggregate data;
- save results in JSON and CSV formats;
- perform requests in parallel.

Supported modes:
- interactive mode (CLI);
- automatic mode (via command-line arguments).

---

## 🚀 Features

- Multiple API support  
- Parallel processing (java.util.concurrent)  
- Periodic API polling  
- Output to JSON and CSV  
- Reading and displaying saved data  
- Extensible architecture  

---

## 🏗️ Architecture

The project is divided into layers:

- CLI layer — Main, CliRunner  
- Application building — ApplicationBuilder  
- Business logic — ApplicationExecutor  
- Multithreading — ThreadManager  
- Data aggregation — Service  
- API interaction — APIClient, Fetcher, DTO  
- File handling — StructuredWriter, FlatWriter, Printer  

The architecture ensures:
- separation of concerns;
- component independence;
- extensibility;
- testability.

---

## ⚙️ Requirements

- Java 24  
- Maven  

---

## 🔨 Build

mvn clean package

Additionally:
mvn compile  
mvn test

Build output:
target/WebScraper-1.0-SNAPSHOT.jar

---

## ▶️ Run

### Interactive mode

java -cp target/WebScraper-1.0-SNAPSHOT.jar com.parfyonoff.webscraper.Main

Stop command:
stop

---

### Automatic mode

java -cp target/WebScraper-1.0-SNAPSHOT.jar com.parfyonoff.webscraper.Main --api hn hh ex --file result.json --n 3 --t 10

Parameters:

- --api — list of APIs  
- --file — output file name  
- --n — number of threads  
- --t — polling interval (seconds)  

---

## 📄 Output formats

- .json — structured format  
- .csv — tabular format  

Invalid file extension will cause an error.

---

## 🧪 Testing

- Unit tests without real network calls  
- Mock objects are used  
- Coverage: ~86%

Report:
target/site/jacoco/index.html

---

## 🔄 Multithreading

Implemented using java.util.concurrent:

- task concurrency limits  
- periodic execution  
- thread-safe file writing  
- graceful shutdown  

---

## 🛑 Shutdown

Command:
stop

The application stops all tasks, terminates threads, and releases resources.

---

## 🔧 Extending the application

### ➕ Adding a new API

1. Create a class implementing APIClient  
2. Define:
   - request URL  
   - query parameters  
   - DTO classes  
3. Use Fetcher for HTTP requests  
4. Map response to internal model  
5. Register the client in Service  

A new API can be added without modifying existing logic.

---

### ➕ Adding a new output format

1. Create a new writer class:
   - similar to StructuredWriter (for structured data)
   - or FlatWriter (for tabular data)
2. Implement write logic  
3. Add format selection based on file extension  

Example:
.json → StructuredWriter  
.csv → FlatWriter  

---

## 📈 Result

Implemented:
- REST API integration  
- data aggregation  
- multithreading  
- extensible architecture  
- testability  

---
