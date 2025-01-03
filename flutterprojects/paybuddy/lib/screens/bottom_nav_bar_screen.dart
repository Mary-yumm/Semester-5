import 'package:flutter/material.dart';
import '../screens/monthly_bills_screen.dart'; // Import your monthly bills screen
import '../screens/profile_screen.dart';
import '../utils/app_colors.dart';

class BottomNavBarScreen extends StatefulWidget {
  @override
  _BottomNavBarScreenState createState() => _BottomNavBarScreenState();
}

class _BottomNavBarScreenState extends State<BottomNavBarScreen> {
  int _currentIndex = 0; // Default to the first screen (Monthly Bills)

  // Titles for AppBar
  final List<String> _titles = [
    'Monthly Bills', // Update the title for the default screen
    'Profile',
  ];

  // Screens for Bottom Navigation Bar
  final List<Widget> _screens = [
    MonthlyBillsScreen(), // Replace HomeScreen with MonthlyBillsScreen
    ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          _titles[_currentIndex],
          style: TextStyle(color: Colors.white),
        ),
        backgroundColor: AppColors.primary,
        centerTitle: true, // Optional: Centers the title
      ),
      body: IndexedStack(
        index: _currentIndex,
        children: _screens,
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        selectedItemColor: AppColors.gold,
        unselectedItemColor: AppColors.background,
        backgroundColor: AppColors.primary, // Custom background color
        items: [
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: 'Monthly Bills', // Update label
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
      ),
    );
  }
}
