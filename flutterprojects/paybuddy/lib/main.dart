// main.dart
import 'package:flutter/material.dart';
import 'screens/bottom_nav_bar_screen.dart';
import 'utils/app_colors.dart';
import 'package:firebase_core/firebase_core.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();  // Initialize Firebase
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'RUHMAR',
      theme: ThemeData(
        primaryColor: Colors.blue,
      ),
      home: BottomNavBarScreen(),
    );
  }
}
