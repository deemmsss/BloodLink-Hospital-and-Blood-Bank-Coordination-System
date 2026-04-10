# BloodLink — Hospital & Blood Bank Coordination System

## Problem Statement

Hospitals and blood banks need to coordinate blood unit requests during emergencies and routine surgeries. Miscommunication between these organizations causes dangerous delays in blood availability, lost requests, and poor inventory tracking. **BloodLink** is a digital platform that streamlines the request-fulfillment pipeline between a hospital and a blood bank, ensuring patients receive the right blood type at the right time.

## Demo Recording

🎥 **[Watch the demo recording here](https://drive.google.com/file/d/142D05rDsaF6kydtEmX0YpOi1Cc_Ir0Ec/view?usp=drive_link)**

## Tech Stack

| Component         | Technology                          |
|-------------------|-------------------------------------|
| Language          | Java 17+                            |
| UI Framework      | Java Swing (Nimbus Look & Feel)     |
| Database          | MySQL 8.0 (Docker-hosted)           |
| DB Connectivity   | JDBC — MySQL Connector/J            |
| IDE               | Apache NetBeans                     |
| Diagrams          | PlantUML                            |
| Version Control   | Git + GitHub                        |

## Enterprises & Roles

| Enterprise            | Role                    | Type   | Responsibilities                                              |
|-----------------------|-------------------------|--------|---------------------------------------------------------------|
| **General Hospital**  | Hospital Nurse          | Normal | Manage patients, create blood requests, confirm receipt       |
| **General Hospital**  | Hospital Admin          | Admin  | Manage nurse accounts, escalate requests, view usage reports  |
| **City Blood Bank**   | Blood Bank Technician   | Normal | Process requests, prepare shipments, view inventory           |
| **City Blood Bank**   | Blood Bank Admin        | Admin  | Manage technicians, manage donors, set inventory thresholds   |

## Work Requests

| #  | Work Request                        | Type             | Flow                                   |
|----|-------------------------------------|------------------|----------------------------------------|
| 1  | Blood Unit Request                  | Cross-enterprise | Nurse → Blood Bank                     |
| 2  | Fulfillment / Shipment Confirmation | Cross-enterprise | Technician → Hospital                  |
| 3  | Escalate / Prioritize Request       | Internal         | Hospital Admin within General Hospital |
| 4  | Manage Donor Records & Inventory    | Internal         | Blood Bank Admin within City Blood Bank|

## Cross-Enterprise Pipeline

```
Nurse creates request    ──►  Blood Bank receives in queue
         │                              │
         │                    Technician approves
         │                              │
         │                    Technician prepares shipment
         │                              │
         │                    Status: SHIPPED  ──►  Nurse sees shipment
         │                                                  │
         │                                        Nurse confirms receipt
         │                                                  │
         ◄──────────────  Status: RECEIVED  ◄───────────────┘
```

**Status Lifecycle:** `PENDING → APPROVED → SHIPPED → RECEIVED` (or `PENDING → REJECTED`)

## Project Structure

```
BloodLink-Hospital-and-Blood-Bank-Coordination-System/
├── BloodLink/                          # NetBeans project root
│   └── src/
│       └── com/bloodlink/
│           ├── main/                   # Application entry point
│           │   └── BloodLinkApp.java
│           ├── model/                  # Entity classes
│           │   ├── User.java (abstract)
│           │   ├── HospitalNurse.java
│           │   ├── HospitalAdmin.java
│           │   ├── BloodBankTechnician.java
│           │   ├── BloodBankAdmin.java
│           │   ├── Patient.java
│           │   ├── Donor.java
│           │   ├── BloodUnit.java
│           │   ├── BloodRequest.java
│           │   ├── Shipment.java
│           │   ├── InventoryThreshold.java
│           │   ├── WorkRequestHandler.java (interface)
│           │   └── AccountManager.java (interface)
│           ├── model/enums/            # Type-safe enumerations
│           │   ├── BloodType.java
│           │   ├── RequestStatus.java
│           │   ├── RequestPriority.java
│           │   └── UserRole.java
│           ├── db/                     # Database connectivity
│           │   └── DBConnection.java
│           ├── dao/                    # Data Access Objects (all SQL)
│           │   ├── UserDAO.java
│           │   ├── PatientDAO.java
│           │   ├── DonorDAO.java
│           │   ├── BloodUnitDAO.java
│           │   ├── BloodRequestDAO.java
│           │   ├── ShipmentDAO.java
│           │   └── InventoryThresholdDAO.java
│           ├── ui/                     # Shared UI (login)
│           │   ├── LoginScreen.java
│           │   └── MainFrame.java
│           ├── ui/hospital/            # Hospital enterprise screens
│           │   ├── NurseDashboardPanel.java
│           │   ├── NursePatientPanel.java
│           │   ├── NurseCreateRequestPanel.java
│           │   ├── NurseMyRequestsPanel.java
│           │   ├── NurseConfirmReceiptPanel.java
│           │   ├── AdminDashboardPanel.java
│           │   ├── AdminManageNursesPanel.java
│           │   ├── AdminEscalatePanel.java
│           │   └── AdminUsageReportPanel.java
│           └── ui/bloodbank/           # Blood Bank enterprise screens
│               ├── TechDashboardPanel.java
│               ├── TechRequestQueuePanel.java
│               ├── TechPrepareShipmentPanel.java
│               ├── TechInventoryPanel.java
│               ├── BBAdminDashboardPanel.java
│               ├── BBAdminManageTechsPanel.java
│               ├── BBAdminManageDonorsPanel.java
│               ├── BBAdminThresholdsPanel.java
│               └── BBAdminRequestOverviewPanel.java
├── db/                                 # SQL schema
│   └── bloodLink_schema.sql
├── screenshots/                        # Application & database screenshots
├── blood_bank_use_case_diagram.png     # PlantUML Use Case Diagram
├── blood_bank_class_diagram.png        # PlantUML Class Diagram
├── demo_recording.md                   # Link to recorded demo
└── README.md
```

## Database Setup

### Prerequisites
- Docker installed and running
- MySQL Workbench

### Steps

1. Start the MySQL Docker container:
   ```bash
   docker run --name bloodlink-mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:8.0
   ```

2. Connect via MySQL Workbench: `localhost:3306`, user `root`, password `root`.

3. Open and execute `db/bloodlink_schema.sql`. This creates the `bloodlink` database, all tables, and seed data.

4. Verify: expand `bloodlink → Tables` — you should see 7 tables.

### Default Login Credentials

| Username   | Password   | Role                    |
|------------|------------|-------------------------|
| `nurse1`   | `password` | Hospital Nurse          |
| `hadmin1`  | `password` | Hospital Admin          |
| `tech1`    | `password` | Blood Bank Technician   |
| `bbadmin1` | `password` | Blood Bank Admin        |

## How to Run

1. Open the `BloodLink/` folder in Apache NetBeans.
2. Ensure `mysql-connector-j-8.x.x.jar` is added to project libraries.
3. Ensure the Docker MySQL container is running.
4. Right-click `BloodLinkApp.java` → **Run File**, or press **F6** after setting it as the main class.

## OOP Design Highlights

- **Abstract class:** `User` with abstract method `getDashboardTitle()`
- **Inheritance:** `HospitalNurse`, `HospitalAdmin`, `BloodBankTechnician`, `BloodBankAdmin` extend `User`
- **Interfaces:** `WorkRequestHandler` (implemented by Technician + BB Admin), `AccountManager` (implemented by Hospital Admin + BB Admin)
- **Multiple interface implementation:** `BloodBankAdmin` implements both interfaces
- **Enumerations:** `BloodType`, `RequestStatus`, `RequestPriority`, `UserRole` with custom fields and methods
- **Encapsulation:** All entity fields are private with public getters/setters
- **DAO pattern:** All SQL is isolated in Data Access Objects, keeping UI code clean

## Screenshots

See the `/screenshots/` folder for:
- All application screens for every role
- MySQL Workbench CRUD evidence for every table