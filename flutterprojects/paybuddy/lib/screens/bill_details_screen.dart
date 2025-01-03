import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import '../utils/app_colors.dart';

class BillDetailsPage extends StatefulWidget {
  final Map<String, dynamic> bill;
  Map<String, Color> buttonColors = {};
  final Function(Map<String, dynamic>) onDelete; // Callback to delete the bill

  BillDetailsPage({required this.bill, required this.onDelete});

  @override
  _BillDetailsPageState createState() => _BillDetailsPageState();
}

class _BillDetailsPageState extends State<BillDetailsPage> {
  void _updateButtonColors() {
    double totalBill = widget.bill['total'];
    int memberCount = widget.bill['members'].length;
    double perPersonShare = memberCount > 0 ? totalBill / memberCount : 0.0;

    widget.bill['members'].forEach((name, member) {
      double paid = (member['paid'] as double?) ?? 0.0;
      widget.buttonColors[name] = (paid >= perPersonShare) ? AppColors.gold : Colors.red;
    });
  }


  void _addMember() {
    TextEditingController memberPaidController = TextEditingController();
    String? selectedName;
    List<String> names = ["Maryam", "Ryms", "Rims", "Rijs", "Youmama", "Hamna"];

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text("Add Member"),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              DropdownButtonFormField<String>(
                value: selectedName,
                hint: Text("Select or type a name"),
                items: names.map((name) {
                  return DropdownMenuItem(
                    value: name,
                    child: Text(name),
                  );
                }).toList(),
                onChanged: (value) {
                  setState(() {
                    selectedName = value;
                  });
                },
                decoration: InputDecoration(
                  labelText: "Member Name",
                  border: OutlineInputBorder(),
                ),
              ),
              SizedBox(height: 10),
              TextField(
                onChanged: (value) {
                  setState(() {
                    selectedName = value;
                  });
                },
                decoration: InputDecoration(
                  labelText: "Or type a new name",
                  border: OutlineInputBorder(),
                ),
              ),
              SizedBox(height: 10),
              TextField(
                controller: memberPaidController,
                decoration: InputDecoration(
                  labelText: "Amount Paid",
                  border: OutlineInputBorder(),
                ),
                keyboardType: TextInputType.number,
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: Text("Cancel"),
            ),
            TextButton(
              onPressed: () {
                if (selectedName != null &&
                    selectedName!.isNotEmpty &&
                    memberPaidController.text.isNotEmpty) {
                  setState(() {
                    double paidAmount = double.parse(memberPaidController.text);
                    widget.bill['members'][selectedName!] = {
                      "paid": paidAmount,
                      "share": 0.0,
                      "resolved": false,
                    };

                    widget.bill['total'] += paidAmount;
                    _updateButtonColors(); // Recalculate button colors
                  });

                  Navigator.pop(context);
                }
              },
              child: Text("Add Member"),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final members = (widget.bill['members'] as Map?)?.cast<String, Map<String, dynamic>>() ?? {};

    double totalBill = widget.bill['total'];
    int memberCount = members.length;
    double perPersonShare = memberCount > 0 ? totalBill / memberCount : 0.0;

    _updateButtonColors(); // Ensure colors are recalculated every build

    return Scaffold(
      appBar: AppBar(
        title: Text(
          widget.bill['name'],
          style: TextStyle(color: Colors.white),
        ),
        backgroundColor: AppColors.primary,
        iconTheme: IconThemeData(color: Colors.white),
        actions: [
          IconButton(
            icon: Icon(Icons.delete, color: Colors.white),
            onPressed: _deleteBill,
          ),
        ],
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Bill Details Section
              Container(
                padding: EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: AppColors.secondary,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('Date: ${widget.bill['date']}',
                        style: TextStyle(color: Colors.white, fontSize: 16)),
                    SizedBox(height: 8),
                    Text('Total: Rs. ${totalBill.toStringAsFixed(2)}',
                        style: TextStyle(
                            color: Colors.white,
                            fontSize: 20,
                            fontWeight: FontWeight.bold)),
                  ],
                ),
              ),
              SizedBox(height: 20),

              // Members Section
              Text(
                'Members:',
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
              ),
              SizedBox(height: 10),
              members.isEmpty
                  ? Center(child: Text('No members added yet'))
                  : Column(
                children: members.entries.map((memberEntry) {
                  final String name = memberEntry.key;
                  final double paid = (memberEntry.value['paid'] as double?) ?? 0.0;
                  double difference = paid - perPersonShare;

                  // Check if the payment is resolved
                  // Safely check or assign the resolved flag
                  bool isResolved = (memberEntry.value['resolved'] as bool?) ?? false;

                  return Container(
                    margin: EdgeInsets.symmetric(vertical: 8),
                    padding: EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(12),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.grey.withOpacity(0.3),
                          blurRadius: 6,
                          offset: Offset(0, 3),
                        ),
                      ],
                    ),
                    child: Row(
                      children: [
                        CircleAvatar(
                          backgroundColor: AppColors.secondary,
                          child: Text(name[0].toUpperCase(), style: TextStyle(color: Colors.white)),
                        ),
                        SizedBox(width: 16),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                name,
                                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                              ),
                              Text('Paid: Rs. ${paid.toStringAsFixed(2)}'),
                              Text(
                                difference >= 0
                                    ? 'Should Get: Rs. ${difference.toStringAsFixed(2)}'
                                    : 'Left to Pay: Rs. ${(-difference).toStringAsFixed(2)}',
                                style: TextStyle(
                                  color: difference >= 0 ? Colors.green : Colors.red,
                                ),
                              ),
                            ],
                          ),
                        ),
                        SizedBox(width: 12),
                        ElevatedButton(
                          onPressed: () {
                            setState(() {
                              // Mark the payment as resolved
                              members[name]?['resolved'] = true;

                              // Update the paid or share amounts to indicate resolution
                              if (difference < 0) {
                                // Member pays the remaining amount
                                members[name]?['paid'] = perPersonShare;
                              } else if (difference > 0) {
                                // Member with surplus gets adjusted
                                members[name]?['paid'] = perPersonShare;
                                members[name]?['share'] = 0.0; // Reset surplus after resolution
                              }

                              // Recalculate button colors
                              _updateButtonColors();
                            });
                          },
                          style: ElevatedButton.styleFrom(
                            backgroundColor: isResolved ? Colors.green : widget.buttonColors[name],
                          ),
                          child: Text(
                            isResolved ? 'Payment Resolved' : (difference >= 0 ? 'Got Payment' : 'Mark Paid'),
                            style: TextStyle(fontSize: 12, color: AppColors.background),
                          ),
                        )


                      ],
                    ),
                  );
                }).toList(),
              ),

            ],
          ),
        ),
      ),
      bottomNavigationBar: Container(
        color: AppColors.primary,
        padding: EdgeInsets.symmetric(vertical: 10),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton.icon(
              onPressed: _addMember,
              icon: Icon(Icons.add),
              label: Text('Add Member'),
              style: ElevatedButton.styleFrom(
                foregroundColor: Colors.white,
                backgroundColor: AppColors.secondary,
                padding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _deleteBill() {
    widget.onDelete(widget.bill); // Pass the current bill to the parent for deletion
    Navigator.pop(context); // Close the current page
  }


}
