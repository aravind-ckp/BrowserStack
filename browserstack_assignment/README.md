# 📰 website El País, a Spanish news outlet Automation – Selenium + Cucumber + TestNG + BrowserStack

This project automates **fetching, processing, translating, and analyzing articles** from the *El Pais Spanish news outlet* Opinion section.  
It uses **Selenium WebDriver**, **Cucumber (BDD)**, and **TestNG** for running both **locally (Chrome)** and **on BrowserStack** in parallel.  
Translations from Spanish to English are done via **RapidAPI Google Translate** API.

---

## 📦 Features

- ✅ Parallel cross‑browser execution (TestNG `parallel="tests"`)
- ✅ Supports **local runs** (ChromeDriver) and **BrowserStack cloud runs**
- ✅ Article translation from Spanish → English (RapidAPI Google Translate)
- ✅ Article image download if available
- ✅ Word frequency analysis on translated titles
- ✅ Page Object Model (POM) design
- ✅ Environment variable–driven secrets (no hardcoded keys)

---

## 🛠 Prerequisites

- **Java 21+**
- **Maven 3.9+**
- Chrome browser (latest recommended)
- ChromeDriver (managed automatically if configured)
- A RapidAPI account for **RapidAPI key**
- BrowserStack account (for cloud runs)

---

## 🔑 Required Environment Variables

This project **does not** store secrets in code.  
Before running tests, set the following variables in your environment or IDE run configuration:

| Variable | Description |
|----------|-------------|
| `RAPIDAPI_KEY`          | RapidAPI key for Google Translate |
| `BROWSERSTACK_USERNAME` | Your BrowserStack username        |
| `BROWSERSTACK_ACCESS_KEY` | Your BrowserStack access key     |

### How to set environment variables

**Mac/Linux (bash/zsh):**

1. [ ] |`export RAPIDAPI_KEY= "your_rapidapi_key_here"`|
2. [ ] |`export BROWSERSTACK_USERNAME= "your_browserstack_username_here"`|
3. [ ] |`export BROWSERSTACK_ACCESS_KEY= "your_browserstack_access_key_here`|

**Tip:**  
In IntelliJ or Eclipse → Edit Run/Debug Configurations → Add these variables in **Environment Variables**.

## 🚀 Running Tests

### **BrowserStack run**
```bash
  mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/browserstack.xml
```
You can adjust `browser`, `os`, and `osVersion` according to your suite configuration.

---

## 📂 Project Structure

```text
├── pom.xml
├── src
│   ├── main/java
│   └── test
│       ├── java
│       │   ├── hooks/Hooks.java
│       │   ├── steps/ElPaisSteps.java
│       │   ├── runner/TestRunner.java
│       │   └── utils/TranslationApiClient.java
│       └── resources
│           ├── features/Elpais.feature
│           ├── browserstack.xml
│           └── testng-browserstack.xml
└── README.md 
```

## 👤 Author
- Kuruba Aravind