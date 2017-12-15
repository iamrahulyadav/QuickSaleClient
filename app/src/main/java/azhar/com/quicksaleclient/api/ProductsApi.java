package azhar.com.quicksaleclient.api;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import azhar.com.quicksaleclient.utils.Constants;

import static azhar.com.quicksaleclient.api.FireBaseAPI.ENVIRONMENT;

/**
 * Created by azharuddin on 22/11/17.
 */

@SuppressWarnings("unchecked")
public class ProductsApi {
    public static DatabaseReference productDBRef = ENVIRONMENT.child(Constants.PRODUCTS);
    public static HashMap<String, Object> products = new HashMap<>();

    public void getProducts() {
        productDBRef.keepSynced(true);
        productDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        products = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        products.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FireError", databaseError.getMessage());
            }
        });
    }
}
