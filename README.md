# Welcome

# Geting Started

Enggo is a Android application that requires several configurations before it can run properly. This guide will walk you through the necessary setup steps to get started.

## Install JDK

FurniShop is built on Java, so you need to have [JDK (Java Development Kit)](https://www.oracle.com/java/technologies/downloads/) installed globally on your machine.

The two most popular versions currently are **JDK 23 (latest version)** and **JDK 21 (LTS)**.

### Download JDK

Different JDK versions are available on Oracle's official website:

- [JDK 23 (Latest)](https://www.oracle.com/java/technologies/downloads/#java23).

- [JDK 21 (LTS)](https://www.oracle.com/java/technologies/downloads/#java21).

There are multiple ways to install JDK. However, for convenience and ease of use, we strongly recommend installing using the provided **installer** files.

> [!NOTE]
> We recommend using JDK 23 for verified compatibility.

After downloading, open the setup file and proceed with the installation as you would with any standard setup file.

In case you wish to change the installation directory, just make sure to note the new directory path.

### Configure Environment Variables

To allow the system to recognize JDK, you must configure environment variables.

#### Configure Environment Variables

1. By default, JDK is installed in:

   > `C:\Program Files\Java\jdk-23`

   or

   > `C:\Program Files\Java\jdk-21`

#### Set Up JAVA_HOME Variable

1. Open the Start Menu, type `Edit the system environment variables`, and press **Enter**.

2. Select **Environment Variables**.

3. Under System Variables, click New...:

- Variable name: **JAVA_HOME**

- Variable value: Enter the JDK installation path, for example:

  - `C:\Program Files\Java\jdk-23`

    > or

  - `C:\Program Files\Java\jdk-21`

4. Click OK to save.

#### Configure PATH Variable

1. In the Environment Variables window, find the Path variable under System Variables.

2. Select Path and click Edit....

3. Click New, then enter the path to the JDK bin directory:

- `C:\Program Files\Java\jdk-23\bin`

  > or

- `C:\Program Files\Java\jdk-21\bin`

4. Click OK.

#### Verify Configuration

After setting up, check if the system recognizes JDK:

1. Open PowerShell or Command Prompt.

2. Run the following command:

```bash
java -version
```

3. Expected output:

```bash
java version "23.0.x" or "21.0.x"
```


## Database Management System (DBMS) Configuration

> [!IMPORTANT]  

Enggo uses **MySQL** as its database management system.

There are two MySQL versions:

- **MySQL Community Edition**: Free version (this is the one we will install).

- **MySQL Enterprise Edition**: A commercial version for enterprises.

The two essential components of MySQL Community are:

- **MySQL Server**: Runs the database

- **MySQL Workbench**: GUI tool for MySQL management

### Download MySQL

Download MySQL from the official website:

1. Visit [MySQL Downloads](https://dev.mysql.com/downloads/).

2. Download `MySQL Installer for Windows`.

3. Ensure you get `mysql-installer-community` NOT `mysql-installer-web-community`.

### Installation and Configuration

#### Run the Installer

1. Open the downloaded **MySQL Installer**.

2. Select **MySQL Server** and **MySQL Workbench**, then click **Next**.

3. Click **Execute** to start the installation process.

4. Set up a `username` and `password` when prompted.

#### Configure MySQL Server

After installation, MySQL is ready to use. However, if you need to change configurations:

1. Open **MySQL Installer**.

2. Select **MySQL Server**.

3. Click **Reconfigure**.

4. Apply desired configuration settings.

Step 3: Verify Installation

1. Open Command Prompt and run the following command to check MySQL Server:

   ```bash
   mysql -u root -p
   ```

2. Enter the password set in the previous step.

3. If installation was successful, the **MySQL CLI** will appear.

## Installation and Running Locally

### Prerequisites

To clone this repo, you need Git installed on your machine, ofcourse !

### Installation

1. Navigate to your desired storage directory.

2. Open PowerShell or Terminal.

3. Run the following command:

   ```bash
   git clone https://github.com/MOPR-Project/enggo
   ```

### Open Project with Android Studio

1. Open **Android Studio**.

2. Click Open.

3. Select the project folder.


### Database Configuration
You can import data from our database schema.

### Run Locally

Simply click `Run` to run the project. After complite, Android will automatically open the browser and load localhost.

<details open>
<summary>Click to see image</summary>    
<pre>
<img src = "https://res.cloudinary.com/dyhjtkde1/image/upload/v1747371963/Screenshot_2025-05-16_115741_nliyvj.png"><img>
</pre>
</details>
