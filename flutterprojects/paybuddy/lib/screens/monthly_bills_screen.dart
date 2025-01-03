import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import 'bill_details_screen.dart';
import '../utils/app_colors.dart';

class MonthlyBillsScreen extends StatefulWidget {
  @override
  _MonthlyBillsScreenState createState() => _MonthlyBillsScreenState();
}

class _MonthlyBillsScreenState extends State<MonthlyBillsScreen> {
  // Default to the current month
  DateTime selectedMonth = DateTime.now();
  List<Map<String, dynamic>> bills = [];

  @override
  Widget build(BuildContext context) {
    final filteredBills = bills.where((bill) {
      final billDate = DateTime.parse(bill['date']);
      return billDate.year == selectedMonth.year && billDate.month == selectedMonth.month;
    }).toList();

    return Scaffold(
      appBar: AppBar(
        title: Text(DateFormat('MMMM yyyy').format(selectedMonth) + ' Bills'),
        actions: [
          IconButton(
            icon: Icon(Icons.calendar_month),
            onPressed: () => _pickMonth(context),
          ),
        ],
        backgroundColor: AppColors.secondary,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Detailed Bills',
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            Expanded(
              child: filteredBills.isEmpty
                  ? Center(child: Text("No bills for this month"))
                  : ListView.builder(
                itemCount: filteredBills.length,
                itemBuilder: (context, index) {
                  final bill = filteredBills[index];
                  return Card(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(15.0), // Set the corner radius.
                    ),
                    clipBehavior: Clip.antiAlias, // Ensures the ListTile respects the Card's borderRadius.
                    margin: EdgeInsets.symmetric(vertical: 8),
                    child: ListTile(
                      tileColor: AppColors.background,
                      title: Text(bill['name']),
                      subtitle: Text(
                        '${bill['date']} - Total: \$${bill['total']}',
                        style: TextStyle(fontSize: 14, color: AppColors.primary),
                      ),
                      trailing: Icon(Icons.arrow_forward),
                      onTap: () {
                        _showBillDetails(context, bill);
                      },
                    ),
                  );

                },
              ),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _addBill,
        child: Icon(Icons.add),
        backgroundColor: AppColors.secondary,
      ),
    );
  }
  // Month picker
  Future<void> _pickMonth(BuildContext context) async {
    final picked = await showDatePicker(
      context: context,
      initialDate: selectedMonth,
      firstDate: DateTime(2000),
      lastDate: DateTime(2100),
      builder: (context, child) {
        return Theme(
          data: ThemeData.light(),
          child: child!,
        );
      },
    );
    if (picked != null) {
      setState(() {
        selectedMonth = DateTime(picked.year, picked.month);
      });
    }
  }

  // Add a new bill dynamically
  void _addBill() {
    TextEditingController billNameController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text("Add New Bill"),
          content: TextField(
            controller: billNameController,
            decoration: InputDecoration(labelText: "Bill Name"),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: Text("Cancel"),
            ),
            TextButton(
              onPressed: () {
                if (billNameController.text.isNotEmpty) {
                  Map<String, dynamic> newBill = {
                    "name": billNameController.text,
                    "date": DateFormat('yyyy-MM-dd').format(DateTime.now()),
                    "total": 0.0,
                    "members": {}
                  };

                  setState(() {
                    bills.add(newBill);
                  });

                  Navigator.pop(context); // Close dialog

                  // Navigate to the bill details page, passing onDelete callback
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => BillDetailsPage(
                        bill: newBill,
                        onDelete: (deletedBill) {
                          setState(() {
                            // Remove the deleted bill from the list
                            bills.removeWhere((existingBill) => existingBill['id'] == deletedBill['id']);
                          });
                        },
                      ),
                    ),
                  );
                }
              },
              child: Text("Add Bill"),
            ),
          ],
        );
      },
    );
  }


  // Show detailed breakdown for a specific bill
  void _showBillDetails(BuildContext context, Map<String, dynamic> bill) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => BillDetailsPage(
          bill: bill,
          onDelete: (deletedBill) {
            setState(() {
              // Remove the deleted bill from the list
              bills.removeWhere((existingBill) => existingBill['id'] == deletedBill['id']);
            });
          },
        ),
      ),
    );
  }


}
