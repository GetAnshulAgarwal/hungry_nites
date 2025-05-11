# College Food Ordering App 🍔📱

A mobile application designed to streamline food ordering for college students from on-campus vendors. This app provides a convenient platform for students to browse food vendors, place orders, and track order status in real-time using Firebase.

## 🚀 Features

- 🔐 **User Authentication**: Separate login flows for students and vendors.
- 🛠️ **Vendor Management**: Vendors can add, edit, and manage their menu items.
- 🛒 **Order Placement**: Students can browse menus, select items, and place orders.
- 📦 **Real-time Order Tracking**: Both students and vendors can view live updates on order status.
- 🕘 **Order History**: Students can see their past orders.
- 🔔 **Push Notifications**: Users are notified in real-time when order statuses change.

## ⚡ Real-time Features

The app leverages **Firebase Realtime Database** and **Firebase Cloud Messaging (FCM)** for real-time functionality:

### 📋 Real-time Menu Updates
- Any changes vendors make to their menu (add/edit/delete) reflect instantly in the student view.

### 🔄 Real-time Order Processing
- Orders placed by students appear instantly on the vendor dashboard.
- Vendors can accept, reject, mark as prepared, or delivered.
- Order status changes reflect immediately on the student’s view.

### 📲 Push Notifications
- Students are notified when the order is accepted, rejected, prepared, or delivered.
- Vendors receive notifications when a new order is placed.
- Notifications work even when the app is in the background or closed.

### 🧾 Real-time Order History
- The order history updates live as the order progresses through its statuses.

---

## 🛠️ How to Run

### ✅ Prerequisites
- Android Studio (latest version)
- JDK 8 or higher
- Android device/emulator (API level 23+)
- Firebase account

### 📥 Setup Instructions

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/college-food-ordering.git


2. **Open the Project:**

   * Launch Android Studio
   * Select **"Open an existing project"**
   * Navigate to the cloned repository

3. **Set Up Firebase:**

   * Go to [Firebase Console](https://console.firebase.google.com)
   * Create a project
   * Add an Android app with package name:

     ```
     com.anshul.collegefoodordering
     ```
   * Download `google-services.json` and place it in the `app/` directory.

4. **Enable Firebase Services:**

   * Authentication (Email/Password)
   * Realtime Database
   * Cloud Messaging

5. **Realtime Database Rules:**

   ```json
   {
   "rules": {
    "users": {
      ".read": "auth != null",  // Allow authenticated users to read all users (needed for vendor listing)
      "$uid": {
        ".write": "$uid === auth.uid"  // Users can only write their own data
      }
    },
    "menuItems": {
      ".read": true,  // Anyone can read menu items (even without login)
      ".write": "auth != null && root.child('users').child(auth.uid).child('userType').val() === 'vendor'"  // Only vendors can write
    },
    "orders": {
      ".read": "auth != null",  // Authenticated users can read orders
      ".write": "auth != null",  // Authenticated users can create orders
      "$orderId": {
        ".validate": "newData.child('studentId').val() === auth.uid || 
                      (data.exists() && 
                       (data.child('vendorId').val() === auth.uid || 
                        data.child('studentId').val() === auth.uid))"  // Validate proper ownership
      }
    }
   }
   }

6. **Build & Run:**

   * Connect a device/emulator
   * Click the **Run** button in Android Studio

---

## 🧪 Testing the App

1. **Create Vendor and Student Accounts:**

   * Sign up as a vendor and add menu items.
   * Sign up as a student and place an order.

2. **Test the Order Flow:**

   * Student places an order.
   * Vendor updates order status (accept, prepare, deliver).
   * Student sees updates in real-time.

3. **Test Notifications:**

   * Change order status.
   * Confirm push notifications are received even in the background.

---

## 🧱 Architecture

* **Frontend**: Native Android (Java)
* **Backend**: Firebase Services

  * Authentication
  * Realtime Database
  * Cloud Messaging
* **Real-time Communication**: Firebase Realtime Listeners + FCM

---

## 👨‍💻 Contributor

* **Anshul Agarwal** – \[Developer]

---

## 📎 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
