package marmu.com.quicksaleclient.modules;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import marmu.com.quicksaleclient.R;
import marmu.com.quicksaleclient.activity.CreateOrderActivity;
import marmu.com.quicksaleclient.adapter.OrderAdapter;
import marmu.com.quicksaleclient.api.FireBaseAPI;
import marmu.com.quicksaleclient.model.OrderModel;


/**
 * Created by azharuddin on 25/7/17.
 */
@SuppressLint("SimpleDateFormat")
@SuppressWarnings({"deprecation", "unchecked"})
public class Order {
    private static List<OrderModel> orderList;

    public static void evaluate(final Context context, View itemView) {

        try {
            final TextView datePicker = itemView.findViewById(R.id.et_date_picker);

            Date currentDate = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(currentDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            if (month <= 9) {
                datePicker.setText("");
                datePicker.append(day + "/" + "0" + (month) + "/" + year);
            } else {
                datePicker.setText("");
                datePicker.append(day + "/" + (month) + "/" + year);
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date pickedDate = formatter.parse(datePicker.getText().toString());

            changeMapToList(context, itemView, pickedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        datePicker(context, itemView);

        createOrder(context, itemView);
    }

    private static void changeMapToList(final Context context, final View itemView, final Date pickedDate) {

        FireBaseAPI.orderDBRef.keepSynced(true);
        FireBaseAPI.orderDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    FireBaseAPI.order = (HashMap<String, Object>) dataSnapshot.getValue();
                    HashMap<String, Object> order = FireBaseAPI.order;
                    orderList = new ArrayList<>();
                    if (order != null) {
                        for (String key : order.keySet()) {
                            HashMap<String, Object> createdOrder = (HashMap<String, Object>) order.get(key);
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                Date salesDate = formatter.parse(createdOrder.get("order_date").toString());
                                if (pickedDate.compareTo(salesDate) == 0) {
                                    orderList.add(new OrderModel(key, createdOrder));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    populateTaken(context, itemView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FireError", databaseError.getMessage());
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private static void datePicker(final Context context, final View itemView) {
        final TextView datePicker = itemView.findViewById(R.id.et_date_picker);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                final int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String pYear = String.valueOf(year);
                                String pMonth = String.valueOf(monthOfYear + 1);
                                String pDay = String.valueOf(dayOfMonth);

                                String cYear = String.valueOf(mYear);
                                String cMonth = String.valueOf(mMonth + 1);
                                String cDay = String.valueOf(mDay);

                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                try {
                                    Date pickedDate = formatter.parse((pDay + "-" + pMonth + "-" + pYear));
                                    Date currentDate = formatter.parse((cDay + "-" + cMonth + "-" + cYear));
                                    if (pickedDate.compareTo(currentDate) <= 0) {
                                        if ((monthOfYear + 1) <= 9) {
                                            datePicker.setText("");
                                            datePicker.append(dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year);
                                        } else {
                                            datePicker.setText("");
                                            datePicker.append(dayOfMonth + "/" +(monthOfYear + 1) + "/" + year);
                                        }
                                        datePicker.clearFocus();
                                        changeMapToList(context, itemView, pickedDate);
                                    } else {
                                        datePicker.setError("Choose Valid date");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private static void populateTaken(Context context, View itemView) {

        OrderAdapter adapter = new OrderAdapter(context, orderList);
        RecyclerView orderView = itemView.findViewById(R.id.rv_orders);
        orderView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        orderView.setLayoutManager(layoutManager);
        orderView.setItemAnimator(new DefaultItemAnimator());
        orderView.setAdapter(adapter);
    }

    private static void createOrder(final Context context, View itemView) {
        TextView createOrder = itemView.findViewById(R.id.btn_create_order);
        createOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateOrderActivity.class);
                context.startActivity(intent);
            }
        });
    }

}
