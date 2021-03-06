package com.cairohat.fragments;


import android.support.annotation.Nullable;
import android.os.Bundle;
import android.os.AsyncTask;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton;

import com.cairohat.R;

import java.util.ArrayList;
import java.util.List;

import com.cairohat.constant.ConstantValues;
import com.cairohat.adapters.ProductAdapter;
import com.cairohat.customs.EndlessRecyclerViewScroll;
import com.cairohat.models.product_model.ProductData;
import com.cairohat.models.product_model.ProductDetails;
import com.cairohat.models.product_model.GetAllProducts;
import com.cairohat.network.APIClient;
import retrofit2.Call;
import retrofit2.Callback;


public class All_Products extends Fragment {

    View rootView;
    
    int pageNo = 1;
    boolean isGridView;
    String customerID;
    String sortBy = "Newest";

    LinearLayout bottomBar;
    LinearLayout sortList;
    TextView emptyRecord;
    TextView sortListText;
    ProgressBar progressBar;
    ToggleButton filterButton;
    ToggleButton toggleLayoutView;
    RecyclerView all_products_recycler;

    ProductAdapter productAdapter;
    List<ProductData> productsList;
    
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;
    String[] sortArray;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.f_products_vertical, container, false);

        if (getArguments() != null) {
            if (getArguments().containsKey("sortBy")) {
                sortBy = getArguments().getString("sortBy");
            }
        }

        // Get the Customer's ID from SharedPreferences
        customerID = getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");


        // Binding Layout Views
        bottomBar = (LinearLayout) rootView.findViewById(R.id.bottomBar);
        sortList = (LinearLayout) rootView.findViewById(R.id.sort_list);
        sortListText = (TextView) rootView.findViewById(R.id.sort_text);
        emptyRecord = (TextView) rootView.findViewById(R.id.empty_record);
        progressBar = (ProgressBar) rootView.findViewById(R.id.loading_bar);
        filterButton = (ToggleButton) rootView.findViewById(R.id.filterBtn);
        toggleLayoutView = (ToggleButton) rootView.findViewById(R.id.layout_toggleBtn);
        all_products_recycler = (RecyclerView) rootView.findViewById(R.id.products_recycler);


        // Hide some of the Views
        filterButton.setVisibility(View.GONE);
        emptyRecord.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    
        
        isGridView = true;
        toggleLayoutView.setChecked(isGridView);


        // Set sortListText text
        if (sortBy.equalsIgnoreCase("top seller")) {
            sortListText.setText(getString(R.string.top_seller));
        } else if (sortBy.equalsIgnoreCase("special")) {
            sortListText.setText(getString(R.string.super_deals));
        } else if (sortBy.equalsIgnoreCase("most liked")) {
            sortListText.setText(getString(R.string.most_liked));
        } else {
            sortListText.setText(getString(R.string.newest));
        }


        
        productsList = new ArrayList<>();
        sortArray = getResources().getStringArray(R.array.sortBy_array);

        // Request for Products based on PageNo.
        RequestAllProducts(pageNo, sortBy);


        // Initialize GridLayoutManager and LinearLayoutManager
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        linearLayoutManager = new LinearLayoutManager(getContext());


        // Initialize the ProductAdapter for RecyclerView
        productAdapter = new ProductAdapter(getContext(), productsList, false);
        
        setRecyclerViewLayoutManager(isGridView);
        all_products_recycler.setAdapter(productAdapter);


        // Handle the Scroll event of Product's RecyclerView
        all_products_recycler.addOnScrollListener(new EndlessRecyclerViewScroll(bottomBar) {
            // Override abstract method onLoadMore() of EndlessRecyclerViewScroll class
            @Override
            public void onLoadMore(int current_page) {
                progressBar.setVisibility(View.VISIBLE);
                // Execute AsyncTask LoadMoreTask to Load More Products from Server
                new LoadMoreTask(current_page).execute();
            }
        });

        productAdapter.notifyDataSetChanged();
    
    
        // Toggle RecyclerView's LayoutManager
        toggleLayoutView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isGridView = isChecked;
                setRecyclerViewLayoutManager(isGridView);
            }
        });


        sortList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get sortBy_array from String_Resources


                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setCancelable(true);
                
                dialog.setItems(sortArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        
                        String selectedText = sortArray[which];
                        sortListText.setText(selectedText);
                        
                        
                        if (selectedText.equalsIgnoreCase(sortArray[0])) {
                            sortBy = sortArray[0];
                        }
                        else if (selectedText.equalsIgnoreCase(sortArray[1])) {
                            sortBy = sortArray[1];
                        }
                        else if (selectedText.equalsIgnoreCase(sortArray[2])) {
                            sortBy = sortArray[2];
                        }
                        else if (selectedText.equalsIgnoreCase(sortArray[3])) {
                            sortBy = sortArray[3];
                        }
                        else if (selectedText.equalsIgnoreCase(sortArray[4])) {
                            sortBy = sortArray[4];
                        }
                        else if (selectedText.equalsIgnoreCase(sortArray[5])) {
                            sortBy = sortArray[5];
                        }
                        else if (selectedText.equalsIgnoreCase(sortArray[6])) {
                            sortBy = sortArray[6];
                        }
                        else if (selectedText.equalsIgnoreCase(sortArray[7])) {
                            sortBy = sortArray[7];
                        }
                        else {
                            sortBy = sortArray[0];
                        }
                        

                        productsList.clear();
                        // Request for Products based on sortBy
                        RequestAllProducts(pageNo, sortBy);
                        dialog.dismiss();



                        // Handle the Scroll event of Product's RecyclerView
                        all_products_recycler.addOnScrollListener(new EndlessRecyclerViewScroll(bottomBar) {
                            // Override abstract method onLoadMore() of EndlessRecyclerViewScroll class
                            @Override
                            public void onLoadMore(int current_page) {
//
                                // Execute AsyncTask LoadMoreTask to Load More Products from Server

                                progressBar.setVisibility(View.VISIBLE);
                                new LoadMoreTask(current_page).execute();
                            }
                        });

                    }
                });
                dialog.show();
            }
        });
        

        return rootView;
        
    }

    

    //*********** Switch RecyclerView's LayoutManager ********//
    
    public void setRecyclerViewLayoutManager(Boolean isGridView) {
        int scrollPosition = 0;
        
        // If a LayoutManager has already been set, get current Scroll Position
        if (all_products_recycler.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) all_products_recycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
        
        productAdapter.toggleLayout(isGridView);
        
        all_products_recycler.setLayoutManager(isGridView ? gridLayoutManager : linearLayoutManager);
        all_products_recycler.setAdapter(productAdapter);
        
        all_products_recycler.scrollToPosition(scrollPosition);
    }



    //*********** Adds Products returned from the Server to the ProductsList ********//

    private void addProducts(List<ProductData> productData) {
        
        // Add Products to ProductsList from the List of ProductData
//        for (int i = 0; i < productData.getProductData().size(); i++) {
//            ProductData productDetails = productData.getProductData().get(i);
//            productsList.add(productDetails);
//        }
        productsList.addAll(productData);

        productAdapter.notifyDataSetChanged();


        // Change the Visibility of emptyRecord Text based on ProductList's Size
        if (productAdapter.getItemCount() == 0) {
            emptyRecord.setVisibility(View.VISIBLE);
        } else {
            emptyRecord.setVisibility(View.GONE);
        }
        
    }



    //*********** Request Products from the Server based on PageNo. ********//

    public void RequestAllProducts(int pageNumber, String sortBy) {
        Call<List<ProductData>> call = null;/*= APIClient.getInstance()
                .getallproduct(pageNumber);*/
        if(sortBy.equalsIgnoreCase(sortArray[0])){
            call = APIClient.getInstance().getallproduct(pageNumber);
//            Toast.makeText(getContext() ,sortArray[0] , Toast.LENGTH_LONG ).show();

        }else if(sortBy.equalsIgnoreCase(sortArray[1])){
            call = APIClient.getInstance().getallproductAtoZ("DESC" , pageNumber);
//            Toast.makeText(getContext() ,sortArray[1] , Toast.LENGTH_LONG ).show();


        }else if(sortBy.equalsIgnoreCase(sortArray[2])){
            call = APIClient.getInstance().getallproductAtoZ("ASC" , pageNumber);
//            Toast.makeText(getContext() ,sortArray[2] , Toast.LENGTH_LONG ).show();


        }else if(sortBy.equalsIgnoreCase(sortArray[3])){
            Toast.makeText(getContext() ,sortArray[3] , Toast.LENGTH_LONG ).show();


        }else if(sortBy.equalsIgnoreCase(sortArray[4])){
            Toast.makeText(getContext() ,sortArray[4] , Toast.LENGTH_LONG ).show();


        }else if(sortBy.equalsIgnoreCase(sortArray[5])){
            call = APIClient.getInstance().getbestsales(pageNumber);
//            Toast.makeText(getContext() ,sortArray[5] , Toast.LENGTH_LONG ).show();


        }else if(sortBy.equalsIgnoreCase(sortArray[6])){
            call = APIClient.getInstance().getproductsdeals(pageNumber);
//            Toast.makeText(getContext() ,sortArray[6] , Toast.LENGTH_LONG ).show();


        }else if(sortBy.equalsIgnoreCase(sortArray[7])){
            Toast.makeText(getContext() ,sortArray[7] , Toast.LENGTH_LONG ).show();


        }

//        GetAllProducts getAllProducts = new GetAllProducts();
//        getAllProducts.setPageNumber(pageNumber);
//        getAllProducts.setLanguageId(ConstantValues.LANGUAGE_ID);
//        getAllProducts.setCustomersId(customerID);
//        getAllProducts.setType(sortBy);


        call.enqueue(new Callback<List<ProductData>>() {
            @Override
            public void onResponse(Call<List<ProductData>> call, retrofit2.Response<List<ProductData>> response) {
                
                if (response.isSuccessful()) {

                       addProducts(response.body());


//                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
//                        // Products have been returned. Add Products to the ProductsList
//                        addProducts(response.body());
//
//                    }
//                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
//                        // Products haven't been returned. Call the method to process some implementations
//                        addProducts(response.body());
//                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
//
//                    }
//                    else {
//                        // Unable to get Success status
//                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
//                    }

                    // Hide the ProgressBar
                    progressBar.setVisibility(View.GONE);
                    
                }
                else {
                    Toast.makeText(getContext(), response.code()+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductData>> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }



    /*********** LoadMoreTask Used to Load more Products from the Server in the Background Thread using AsyncTask ********/

    private class LoadMoreTask extends AsyncTask<String, Void, String> {

        int page_number;


        private LoadMoreTask(int page_number) {
            this.page_number = page_number;
        }


        //*********** Runs on the UI thread before #doInBackground() ********//

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        //*********** Performs some Processes on Background Thread and Returns a specified Result  ********//

        @Override
        protected String doInBackground(String... params) {

            // Request for Products based on PageNo.
            RequestAllProducts(page_number, sortBy);


            return "All Done!";
        }


        //*********** Runs on the UI thread after #doInBackground() ********//

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}