# វចនានុក្រមទិន្នន័យ (Data Dictionary)
# Beckie Deal E-Commerce Mobile Application

---

## 4.3.4 វចនានុក្រមទិន្នន័យ

បន្ទាប់ពីបានវិភាគលើប្រព័ន្ធខាងលើរួចមក ក្រុមសិក្សាស្រាវជ្រាវអាចសិក្សាកាន់តែស៊ីជម្រៅ និងច្បាស់លាស់ទៅលើប្រព័ន្ធ Data Dictionary ដែលក្នុងនោះរួមមាន៖

- **External Entity** - អង្គភាពខាងក្រៅ
- **Data Element Description** - ការពិពណ៌នាធាតុទិន្នន័យ
- **Process Description** - ការពិពណ៌នាដំណើរការ
- **Data Flow** - លំហូរទិន្នន័យ
- **Data Store** - ឃ្លាំងទិន្នន័យ

---

## ក. External Entity (អង្គភាពខាងក្រៅ)

### DATA DICTIONARY FORM - EXTERNAL ENTITY

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Client  
**ALIAS NAME:** Customer, អតិថិជន  
**DESCRIPTION:** អ្នកប្រើប្រាស់ទូទៅដែលទិញផលិតផលតាមរយៈ Mobile Application

**INPUT DATA FLOW:**
- Client Register (ព័ត៌មានចុះឈ្មោះ)
- Login Information (Username, Password)
- Search Product (ស្វែងរកផលិតផល)
- View Product Details
- Make Orders (បញ្ជាទិញ)
- Upload Personal Document

**OUTPUT DATA FLOW:**
- Products Information (ព័ត៌មានផលិតផល)
- Order Confirmation (ការបញ្ជាក់ការបញ្ជាទិញ)
- Order History (ប្រវត្តិការបញ្ជាទិញ)
- Product Documents
- User Profile Data

---

### DATA DICTIONARY FORM - EXTERNAL ENTITY

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** User  
**ALIAS NAME:** Staff, បុគ្គលិក
**DESCRIPTION:** បុគ្គលិកក្រុមហ៊ុនដែលមានសិទ្ធិគ្រប់គ្រងមួយចំនួន (គ្រប់គ្រងតាមរយៈ Web Application)

**INPUT DATA FLOW:**
- Login Information
- User Management Data

**OUTPUT DATA FLOW:**
- User List
- User Information
- Role Assignment Data

**NOTE:** User (Staff) មិនប្រើប្រាស់ Mobile Application។ គ្រប់គ្រងតាមរយៈ Web Application តែប៉ុណ្ណោះ។

---

### DATA DICTIONARY FORM - EXTERNAL ENTITY

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Admin  
**ALIAS NAME:** Administrator, អ្នកគ្រប់គ្រង  
**DESCRIPTION:** ធ្វើការត្រួតពិនិត្យរាល់ព័ត៌មាន គ្រប់គ្រងផលិតផល ការបញ្ជាទិញ និងកំណត់តួនាទីរបស់អ្នកប្រើប្រាស់

**INPUT DATA FLOW:**
- Product Management (Create/Update/Delete Products)
- Category Management
- Order Control
- User Management
- Set Role/Position
- View Reports

**OUTPUT DATA FLOW:**
- Product Information
- Category Data
- Order Status Updates
- User Information
- Report Data

**NOTE:** Admin មិនប្រើប្រាស់ Mobile Application។ គ្រប់គ្រងតាមរយៈ Web Application តែប៉ុណ្ណោះ។

---

## ខ. Data Element Description (ការពិពណ៌នាធាតុទិន្នន័យ)

### DATA DICTIONARY FORM - DATA ELEMENT DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** user_id  
**ALTERNATIVE:** id, User ID  
**TYPE AND LENGTH:** bigserial (BigInt)  
**DESCRIPTION:** លេខសម្គាល់តែមួយគត់សម្រាប់អ្នកប្រើប្រាស់នីមួយៗ (Primary Key)  
**FORM COLUMN HEADER:** ID

---

### DATA DICTIONARY FORM - DATA ELEMENT DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** email  
**ALTERNATIVE:** user_email, Email Address  
**TYPE AND LENGTH:** varchar(255)  
**DESCRIPTION:** អាសយដ្ឋានអ៊ីមែលរបស់អ្នកប្រើប្រាស់ (តម្រូវឲ្យមាន, Unique)  
**FORM COLUMN HEADER:** Email

---

### DATA DICTIONARY FORM - DATA ELEMENT DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** password  
**ALTERNATIVE:** user_password  
**TYPE AND LENGTH:** varchar(255)  
**DESCRIPTION:** លេខសម្ងាត់របស់អ្នកប្រើប្រាស់ (Hashed/Encrypted)  
**FORM COLUMN HEADER:** Password

---

### DATA DICTIONARY FORM - DATA ELEMENT DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** first_name  
**ALTERNATIVE:** firstname  
**TYPE AND LENGTH:** varchar(255)  
**DESCRIPTION:** នាមខ្លួនរបស់អ្នកប្រើប្រាស់  
**FORM COLUMN HEADER:** First Name

---

### DATA DICTIONARY FORM - DATA ELEMENT DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application
**LABEL:** product_id  
**ALTERNATIVE:** id, Product ID  
**TYPE AND LENGTH:** bigserial (BigInt)  
**DESCRIPTION:** លេខសម្គាល់តែមួយគត់សម្រាប់ផលិតផលនីមួយៗ (Primary Key)  
**FORM COLUMN HEADER:** Product ID

---

### DATA DICTIONARY FORM - DATA ELEMENT DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** product_name  
**ALTERNATIVE:** name  
**TYPE AND LENGTH:** varchar(255)  
**DESCRIPTION:** ឈ្មោះផលិតផល  
**FORM COLUMN HEADER:** Product Name

---

### DATA DICTIONARY FORM - DATA ELEMENT DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** price  
**ALTERNATIVE:** product_price  
**TYPE AND LENGTH:** numeric(15, 2)  
**DESCRIPTION:** តម្លៃផលិតផលជាដុល្លារ ($)  
**FORM COLUMN HEADER:** Price

---

### DATA DICTIONARY FORM - DATA ELEMENT DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** sku  
**ALTERNATIVE:** product_sku, Stock Keeping Unit  
**TYPE AND LENGTH:** varchar(255)  
**DESCRIPTION:** លេខកូដផលិតផល (Unique)  
**FORM COLUMN HEADER:** SKU

---

### DATA DICTIONARY FORM - DATA ELEMENT DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** stock_quantity  
**ALTERNATIVE:** quantity, stock
**TYPE AND LENGTH:** integer  
**DESCRIPTION:** ចំនួនផលិតផលនៅក្នុងស្តុក  
**FORM COLUMN HEADER:** Stock

---

### DATA DICTIONARY FORM - DATA ELEMENT DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** order_number  
**ALTERNATIVE:** order_id  
**TYPE AND LENGTH:** varchar(255)  
**DESCRIPTION:** លេខការបញ្ជាទិញ (Unique)  
**FORM COLUMN HEADER:** Order Number

---

### DATA DICTIONARY FORM - DATA ELEMENT DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** grand_total  
**ALTERNATIVE:** total_amount  
**TYPE AND LENGTH:** numeric(15, 2)  
**DESCRIPTION:** តម្លៃសរុបនៃការបញ្ជាទិញ  
**FORM COLUMN HEADER:** Total

---

## គ. Process Description (ការពិពណ៌នាដំណើរការ)

### DATA DICTIONARY FORM - PROCESS DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Client Register (Process 4)  
**PURPOSE:** កំឡុងពេលដែល Client ប្រើប្រាស់ Mobile Application ត្រូវធ្វើការចុះឈ្មោះគណនីថ្មី

**INPUT DATA FLOW:**
- first_name (នាមខ្លួន)
- last_name (នាមត្រកូល)
- email (អ៊ីមែល)
- password (លេខសម្ងាត់)
- phone (លេខទូរស័ព្ទ)

**OUTPUT DATA FLOW:**
- Account Created Confirmation
- User Profile Data

**DESCRIPTION:** Client បំពេញព័ត៌មានផ្ទាល់ខ្លួន និងបង្កើតគណនីថ្មី។ ព័ត៌មានត្រូវបានរក្សាទុកក្នុង D5: User Table។

---

### DATA DICTIONARY FORM - PROCESS DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Client Login (Process 3)  
**PURPOSE:** Client អាចធ្វើការ Login ចូលទៅក្នុង Account ផ្ទាល់ខ្លួនក្នុង Mobile Application

**INPUT DATA FLOW:**
- email (Username)
- password (Password)

**OUTPUT DATA FLOW:**
- Authentication Token (JWT)
- User Profile Data
- Success/Error Message

**DESCRIPTION:** Client ដែលបាន Register រួចអាចធ្វើការ Login ដោយធ្វើការបំពេញ Email និង Password ផ្ទាល់ខ្លួន។ ប្រព័ន្ធធ្វើការត្រួតពិនិត្យជាមួយ D4: User Table។

---

### DATA DICTIONARY FORM - PROCESS DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Logout (Process 3)  
**PURPOSE:** Client អាចធ្វើការ Logout ចាកចេញពីប្រព័ន្ធ

**INPUT DATA FLOW:**
- User Session Token

**OUTPUT DATA FLOW:**
- Logout Confirmation
- Clear Session Data

**DESCRIPTION:** ប្រព័ន្ធធ្វើការលុប Session និង Token របស់អ្នកប្រើប្រាស់ ដើម្បីរក្សាសុវត្ថិភាព។

---

### DATA DICTIONARY FORM - PROCESS DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Search Product  
**PURPOSE:** Client អាចស្វែងរកផលិតផលតាមឈ្មោះ, Brand, Category, Specs, SKU

**INPUT DATA FLOW:**
- Search Query (ពាក្យស្វែងរក)
- Filter Criteria (លក្ខខណ្ឌច្រោះតម)

**OUTPUT DATA FLOW:**
- Product List (បញ្ជីផលិតផល)
- Product Count

**DESCRIPTION:** ប្រព័ន្ធស្វែងរកផលិតផលពី D1: Products Table តាម Query ដែល Client បញ្ចូល។

---

### DATA DICTIONARY FORM - PROCESS DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** View Product Details  
**PURPOSE:** Client អាចមើលព័ត៌មានលម្អិតរបស់ផលិតផល

**INPUT DATA FLOW:**
- product_id (លេខសម្គាល់ផលិតផល)

**OUTPUT DATA FLOW:**
- Product Name, Price, SKU
- Product Images
- Specifications
- Stock Status
- Reviews & Ratings

**DESCRIPTION:** ប្រព័ន្ធទាញយកព័ត៌មានពី D1: Products Table និងបង្ហាញលម្អិតទាំងអស់។

---

### DATA DICTIONARY FORM - PROCESS DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Make Orders (Process 2)  
**PURPOSE:** Client អាចបញ្ជាទិញផលិតផល

**INPUT DATA FLOW:**
- Cart Items (ផលិតផលក្នុងកន្ត្រក)
- Delivery Address
- Payment Method
- Shipping Method

**OUTPUT DATA FLOW:**
- Order Number
- Order Confirmation
- Order Summary
- Payment Receipt

**DESCRIPTION:** ប្រព័ន្ធបង្កើតការបញ្ជាទិញថ្មីនៅក្នុង D3: Orders Table និង order_items table។ Cart Items ត្រូវបានផ្ទេរទៅជា Order Items។

---

### DATA DICTIONARY FORM - PROCESS DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Add to Cart  
**PURPOSE:** Client អាចបញ្ចូលផលិតផលទៅក្នុងកន្ត្រក

**INPUT DATA FLOW:**
- product_id
- quantity (បរិមាណ)
- user_id (សម្រាប់ logged-in users)

**OUTPUT DATA FLOW:**
- Cart Updated Confirmation
- Cart Badge Count

**DESCRIPTION:** ប្រព័ន្ធបញ្ចូលផលិតផលទៅក្នុង carts និង cart_items tables។

---

### DATA DICTIONARY FORM - PROCESS DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Upload Document (Process 7)  
**PURPOSE:** Client អាចធ្វើការ Upload ឯកសារផ្ទាល់ខ្លួន (ប្រសិនបើមាន)

**INPUT DATA FLOW:**
- Document File (ឯកសារ)
- Document Type (ប្រភេទឯកសារ)
- user_id

**OUTPUT DATA FLOW:**
- Upload Success/Failure
- Document URL

**DESCRIPTION:** ប្រព័ន្ធរក្សាទុកឯកសារក្នុង D6: Document Table។

---

### DATA DICTIONARY FORM - PROCESS DESCRIPTION

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** View Document (Process 7)  
**PURPOSE:** Client អាចមើល និង Download ឯកសារផលិតផល

**INPUT DATA FLOW:**
- product_id
- document_id

**OUTPUT DATA FLOW:**
- Document File
- Document Information

**DESCRIPTION:** ប្រព័ន្ធទាញយកឯកសារពី D6: Document Table និងបង្ហាញឲ្យ Client។

---

## ឃ. Data Flow (លំហូរទិន្នន័យ)

### DATA DICTIONARY FORM - DATA FLOW

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Login  
**DESCRIPTION:** Client អាច Login ចូលប្រើប្រាស់ប្រព័ន្ធ  
**ORIGIN:** Client Login Screen  
**DESTINATION:** Process 3 (Login/Logout) → D4: User Table  
**RECORD:** email, password  
**COMMENTS:** ត្រួតពិនិត្យ Authentication និង Session Management

---

### DATA DICTIONARY FORM - DATA FLOW

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Client Register
**DESCRIPTION:** Client ចុះឈ្មោះគណនីថ្មី  
**ORIGIN:** Client Register Screen  
**DESTINATION:** Process 4 (Create Account) → D5: User Table  
**RECORD:** first_name, last_name, email, password, phone  
**COMMENTS:** បង្កើតគណនីអ្នកប្រើប្រាស់ថ្មីក្នុង Database

---

### DATA DICTIONARY FORM - DATA FLOW

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Products Information  
**DESCRIPTION:** ព័ត៌មានផលិតផលទាំងអស់  
**ORIGIN:** D1: Products Table  
**DESTINATION:** Client (Mobile App Screens)  
**RECORD:** product_id, name, price, sku, stock_quantity, images, specs  
**COMMENTS:** បង្ហាញផលិតផលនៅលើ Home, Category, Search Screens

---

### DATA DICTIONARY FORM - DATA FLOW

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Category Data  
**DESCRIPTION:** ទិន្នន័យប្រភេទផលិតផល  
**ORIGIN:** D2: Category Table  
**DESTINATION:** Client (Category Screen)  
**RECORD:** category_id, name, slug, image, product_count  
**COMMENTS:** បង្ហាញប្រភេទផលិតផលសម្រាប់ Client ជ្រើសរើស

---

### DATA DICTIONARY FORM - DATA FLOW

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Orders Data  
**DESCRIPTION:** ទិន្នន័យការបញ្ជាទិញ  
**ORIGIN:** Process 2 (Make Orders)  
**DESTINATION:** D3: Orders Table + order_items Table  
**RECORD:** order_number, user_id, grand_total, status, items  
**COMMENTS:** រក្សាទុកការបញ្ជាទិញថ្មី

---

### DATA DICTIONARY FORM - DATA FLOW

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Order History  
**DESCRIPTION:** ប្រវត្តិការបញ្ជាទិញ
**ORIGIN:** D3: Orders Table  
**DESTINATION:** Client (My Orders Screen)  
**RECORD:** order_number, date, status, total_amount  
**COMMENTS:** Client អាចមើលការបញ្ជាទិញទាំងអស់របស់ខ្លួន

---

### DATA DICTIONARY FORM - DATA FLOW

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Cart Data  
**DESCRIPTION:** ទិន្នន័យកន្ត្រកទិញទំនិញ  
**ORIGIN:** carts Table + cart_items Table  
**DESTINATION:** Client (Cart Screen)  
**RECORD:** cart_id, product_id, quantity, subtotal, grand_total  
**COMMENTS:** បង្ហាញផលិតផលក្នុងកន្ត្រក និងគណនាតម្លៃសរុប

---

### DATA DICTIONARY FORM - DATA FLOW

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Wishlist Data  
**DESCRIPTION:** បញ្ជីផលិតផលចូលចិត្ត  
**ORIGIN:** wishlists Table + wishlist_items Table  
**DESTINATION:** Client (Favorites Screen)  
**RECORD:** wishlist_id, product_id, product_info  
**COMMENTS:** រក្សាទុក និងបង្ហាញផលិតផលដែល Client ចូលចិត្ត

---

### DATA DICTIONARY FORM - DATA FLOW

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** Document Data  
**DESCRIPTION:** ឯកសារផលិតផល និងឯកសារផ្ទាល់ខ្លួន  
**ORIGIN:** Process 7 (Document) → D6: Document Table  
**DESTINATION:** Client (Document Viewer)  
**RECORD:** document_id, file_path, file_type, product_id  
**COMMENTS:** រក្សាទុក និងបង្ហាញឯកសារផលិតផល

---

## ង. Data Store (ឃ្លាំងទិន្នន័យ)

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** D1: Products  
**ABBREVIATION:** products Table
**DESCRIPTION:** Database Store ព័ត៌មានផលិតផលទាំងអស់

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- category_id (Foreign Key → categories)
- brand_id (Foreign Key → brands)
- name (varchar 255)
- slug (varchar 255, Unique)
- sku (varchar 255, Unique)
- price (numeric 15,2)
- sale_price (numeric 15,2)
- stock_quantity (integer)
- description (text)
- thumbnail (varchar 255)
- is_active (boolean)
- is_featured (boolean)
- created_at, updated_at (timestamp)

**COMMENTS:** រក្សាទុកព័ត៌មានផលិតផលទាំងអស់សម្រាប់ការលក់

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** D2: Category  
**ABBREVIATION:** categories Table  
**DESCRIPTION:** Database Store ប្រភេទផលិតផល

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- parent_id (Foreign Key → categories)
- name (varchar 255)
- slug (varchar 255, Unique)
- description (text)
- image (varchar 255)
- icon (varchar 255)
- is_active (boolean)
- is_featured (boolean)
- sort_order (integer)

**COMMENTS:** រក្សាទុកប្រភេទផលិតផល និង Subcategories

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** D3: Orders  
**ABBREVIATION:** orders Table  
**DESCRIPTION:** Database Store ការបញ្ជាទិញរបស់ Client

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- user_id (Foreign Key → users)
- address_id (Foreign Key → addresses)
- order_number (varchar 255, Unique)
- subtotal (numeric 15,2)
- discount_total (numeric 15,2)
- tax_total (numeric 15,2)
- shipping_total (numeric 15,2)
- grand_total (numeric 15,2)
- payment_method (varchar 255)
- payment_status (varchar 255)
- status (varchar 255)
- shipping_status (varchar 255)
- created_at, updated_at (timestamp)

**COMMENTS:** រក្សាទុកការបញ្ជាទិញពី Client តាមរយៈ Process 2

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** D4: User (Authentication)  
**ABBREVIATION:** users Table  
**DESCRIPTION:** Database Store ព័ត៌មានអ្នកប្រើប្រាស់សម្រាប់ Authentication

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- first_name (varchar 255)
- last_name (varchar 255)
- username (varchar 255, Unique)
- email (varchar 255, Unique)
- password (varchar 255, Hashed)
- phone (varchar 255, Unique)
- role (varchar 255, Default: 'user')
- is_active (boolean)
- last_login_at (timestamp)
- created_at, updated_at (timestamp)

**COMMENTS:** ប្រើសម្រាប់ Process 3 (Login/Logout System)

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** D5: User (Registration)  
**ABBREVIATION:** users Table  
**DESCRIPTION:** Database Store ព័ត៌មានគណនីអ្នកប្រើប្រាស់

**RECORD STRUCTURE:**
- (Same as D4 - Same Table)

**COMMENTS:** ប្រើសម្រាប់ Process 4 (Client Register/Create Account)

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** D6: Document  
**ABBREVIATION:** Not found in schema (To be created)  
**DESCRIPTION:** Database Store ឯកសារផលិតផល និងឯកសារផ្ទាល់ខ្លួន

**RECORD STRUCTURE (Proposed):**
- id (bigserial, Primary Key)
- product_id (Foreign Key → products, nullable)
- user_id (Foreign Key → users, nullable)
- file_path (varchar 255)
- file_name (varchar 255)
- file_type (varchar 255)
- file_size (bigint)
- document_type (varchar 255) // 'product_manual', 'user_document', etc.
- is_active (boolean)
- created_at, updated_at (timestamp)

**COMMENTS:** ប្រើសម្រាប់ Process 7 (View/Upload Documents)

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** carts  
**ABBREVIATION:** carts Table  
**DESCRIPTION:** Database Store ព័ត៌មានកន្ត្រកទិញទំនិញរបស់ Client

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- user_id (Foreign Key → users)
- session_id (uuid, Unique)
- subtotal (numeric 15,2)
- discount_total (numeric 15,2)
- tax_total (numeric 15,2)
- shipping_total (numeric 15,2)
- grand_total (numeric 15,2)
- status (varchar 255)
- expires_at (timestamp)
- created_at, updated_at (timestamp)

**COMMENTS:** រក្សាទុកកន្ត្រកទិញទំនិញរបស់ Client

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** cart_items  
**ABBREVIATION:** cart_items Table  
**DESCRIPTION:** Database Store ផលិតផលក្នុងកន្ត្រក

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- cart_id (Foreign Key → carts)
- product_id (Foreign Key → products)
- product_variant_id (Foreign Key → product_variants)
- quantity (integer)
- unit_price (numeric 15,2)
- subtotal (numeric 15,2)
- total (numeric 15,2)
- product_name (varchar 255)
- is_available (boolean)
- created_at, updated_at (timestamp)

**COMMENTS:** ផ្ទុកបញ្ជីផលិតផលក្នុងកន្ត្រកនីមួយៗ

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** wishlists  
**ABBREVIATION:** wishlists Table  
**DESCRIPTION:** Database Store បញ្ជីផលិតផលចូលចិត្ត

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- user_id (Foreign Key → users)
- session_id (uuid)
- name (varchar 255, Default: 'My Wishlist')
- description (text)
- is_public (boolean)
- is_active (boolean)
- created_at, updated_at (timestamp)

**COMMENTS:** រក្សាទុក Wishlist/Favorites របស់ Client

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** wishlist_items  
**ABBREVIATION:** wishlist_items Table  
**DESCRIPTION:** Database Store ផលិតផលក្នុងបញ្ជីចូលចិត្ត

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- wishlist_id (Foreign Key → wishlists)
- product_id (Foreign Key → products)
- product_variant_id (Foreign Key → product_variants)
- quantity (integer)
- created_at, updated_at (timestamp)

**COMMENTS:** ផ្ទុកផលិតផលដែល Client ចូលចិត្ត

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** order_items  
**ABBREVIATION:** order_items Table  
**DESCRIPTION:** Database Store ផលិតផលក្នុងការបញ្ជាទិញ

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- order_id (Foreign Key → orders)
- product_id (Foreign Key → products)
- product_variant_id (Foreign Key → product_variants)
- quantity (integer)
- unit_price (numeric 15,2)
- subtotal (numeric 15,2)
- total (numeric 15,2)
- product_name (varchar 255)
- product_sku (varchar 255)
- status (varchar 255)
- created_at, updated_at (timestamp)

**COMMENTS:** ផ្ទុកបញ្ជីផលិតផលក្នុងការបញ្ជាទិញនីមួយៗ

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** addresses  
**ABBREVIATION:** addresses Table  
**DESCRIPTION:** Database Store អាសយដ្ឋានដឹកជញ្ជូនរបស់ Client

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- user_id (Foreign Key → users)
- type (varchar 255, Default: 'shipping')
- full_name (varchar 255)
- phone (varchar 255)
- address_line_1 (varchar 255)
- address_line_2 (varchar 255)
- city (varchar 255)
- postal_code (varchar 255)
- country (varchar 255, Default: 'Cambodia')
- is_default (boolean)
- is_active (boolean)
- created_at, updated_at (timestamp)

**COMMENTS:** រក្សាទុកអាសយដ្ឋានដឹកជញ្ជូនរបស់ Client

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** brands  
**ABBREVIATION:** brands Table
**DESCRIPTION:** Database Store ព័ត៌មាន Brand ផលិតផល

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- name (varchar 255)
- slug (varchar 255, Unique)
- logo (varchar 255)
- website (varchar 255)
- description (text)
- is_active (boolean)
- is_featured (boolean)
- products_count (bigint)
- created_at, updated_at (timestamp)

**COMMENTS:** រក្សាទុក Brand (Apple, ASUS, Samsung, Dell, HP, etc.)

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** reviews  
**ABBREVIATION:** reviews Table  
**DESCRIPTION:** Database Store ការវាយតម្លៃផលិតផល

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- user_id (Foreign Key → users)
- product_id (Foreign Key → products)
- order_id (Foreign Key → orders)
- rating (smallint, 1-5)
- title (varchar 255)
- comment (text)
- status (varchar 255, Default: 'pending')
- is_verified_purchase (boolean)
- helpful_count (integer)
- images (json)
- created_at, updated_at (timestamp)

**COMMENTS:** រក្សាទុកការវាយតម្លៃផលិតផលពី Client

---

### DATA DICTIONARY FORM - DATA STORE

**SYSTEM:** Beckie Deal E-Commerce Mobile Application  
**LABEL:** payments  
**ABBREVIATION:** payments Table  
**DESCRIPTION:** Database Store ព័ត៌មានការទូទាត់ប្រាក់

**RECORD STRUCTURE:**
- id (bigserial, Primary Key)
- order_id (Foreign Key → orders)
- user_id (Foreign Key → users)
- payment_reference (varchar 255, Unique)
- provider (varchar 255)
- amount (numeric 15,2)
- currency (varchar 255, Default: 'USD')
- status (varchar 255)
- payment_method (varchar 255)
- paid_at (timestamp)
- created_at, updated_at (timestamp)

**COMMENTS:** រក្សាទុកព័ត៌មានការទូទាត់ប្រាក់

---

## សេចក្តីសន្និដ្ឋាន

វចនានុក្រមទិន្នន័យនេះបង្ហាញយ៉ាងច្បាស់លាស់អំពី៖

✓ **External Entities** - Client, User (Staff), Admin  
✓ **Data Elements** - ធាតុទិន្នន័យសំខាន់ៗដូចជា user_id, email, password, product_id, order_number  
✓ **Processes** - Login, Register, Search, Make Orders, Upload Documents  
✓ **Data Flows** - លំហូរទិន្នន័យរវាង Client និង Database  
✓ **Data Stores** - Database Tables ទាំងអស់ដែល Mobile App ប្រើប្រាស់  

ព័ត៌មានទាំងនេះត្រូវបានប្រើដើម្បីយល់ដឹងយ៉ាងស៊ីជម្រៅអំពីរចនាសម្ព័ន្ធនៃ Beckie Deal E-Commerce Mobile Application។

---

**Created for:** Beckie Deal E-Commerce Project  
**Document Version:** 1.0  
**Last Updated:** 2026
