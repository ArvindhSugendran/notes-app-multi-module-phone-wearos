# 📝 Notes – Secure Notes with Wear OS Sync

A simple yet powerful Android application to create, manage, and securely access personal notes, designed with a focus on privacy, speed, and seamless cross-device experience.

Notes enables users to store important information with PIN-protected access, while maintaining a clean, distraction-free UI. With built-in Recycle Bin recovery and real-time sync between phone and smartwatch, your notes are always safe and accessible.

## ✨ Highlights

🔐 **Secure by Design** – PIN-protected access for private notes  
📱 **Phone + Wear OS Support** – Full multi-device experience  
🔄 **Real-time Sync** – Seamless data sync using Wearable Data Layer API  
🗑️ **Recycle Bin** – Recover accidentally deleted notes  
⚡ **Lightweight & Fast** – Minimal UI with optimized performance  
🧩 **Clean MVVM Architecture** with scalable structure  

## 🔄 Sync System
```
The app uses the Wearable Data Layer API to enable:
✦ Real-time synchronization between phone and smartwatch
✦ Consistent data across devices
✦ Background updates via Wearable Listener Service
```

🚀 Features
```
✦ PIN-protected authentication for secure access
✦ Create, edit, and delete text-based notes
✦ Recycle Bin for restoring deleted notes
✦ Cross-device sync (Phone ↔ Wear OS)
✦ Lightweight and intuitive UI/UX
✦ Offline-first data handling with Room DB
```

🧠 Architecture

This project follows MVVM (Model-View-ViewModel) with a clean separation of concerns:
```
UI (XML)
   ↓
ViewModel (LiveData)
   ↓
Repository
   ↓
Data Layer (Room DB + Firebase + Wearable APIs)
```

Key Principles
```
✦ Separation of concerns
✦ Lifecycle-aware components (LiveData)
✦ Dependency Injection using Hilt
✦ Scalable and maintainable architecture
✦ Offline-first approach with local persistence
```

🛠️ Tech Stack
```
✦ Language: Java
✦ UI: XML
✦ Architecture: MVVM
✦ State Management: LiveData
✦ Dependency Injection: Hilt
✦ Database: Room
✦ Cloud / Sync: Firebase
✦ Wear OS Integration:
	•	Wearable Data Layer API
	•	Wearable Listener Service
```

🎯 Key Engineering Highlights
```
✦ Multi-module architecture (Phone + Wear OS)
✦ Cross-device synchronization system
✦ Secure access layer with PIN protection
✦ Recycle bin lifecycle management
✦ Integration with Wearable APIs for real-time communication
✦ Optimized for performance and low resource usage
```

🏫 Learnings

This project helped in gaining hands-on experience with:
```
✦ Building multi-device Android apps (Phone + Wear OS)
✦ Implementing real-time data sync using Wearable APIs
✦ Designing secure user flows (PIN-based authentication)
✦ Structuring apps using MVVM architecture
✦ Managing local persistence with Room Database
✦ Integrating Firebase for backend support
✦ Handling lifecycle-aware data with LiveData
```

> ## ⚠️ Note

Some legacy dependencies used in this project may not resolve with the latest Gradle versions due to deprecation of JCenter and changes in dependency hosting. The core architecture, UI systems, and features remain fully functional and represent the primary focus of this project.

👨‍💻 Development
```
✦ Developed from scratch
✦ Focused on simplicity, performance, and usability
✦ Built as a real-world application with cross-device capabilities
```

👨‍💻 Author  
Arvindh Prasadh Sugendran
