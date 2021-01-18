package com.uit.quanlychitieu.ui.user.listuser;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.quanlychitieu.MainActivity;
import com.uit.quanlychitieu.model.CategoryModel;
import com.uit.quanlychitieu.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class ListUserViewModel extends ViewModel {
    private MutableLiveData<List<UserModel>> listUserLiveData;
    private List<UserModel> listUser;

    public ListUserViewModel() {
        listUserLiveData = new MutableLiveData<>();
        initData();
    }

    private void initData() {
        listUser = new ArrayList<>(MainActivity.users);
        listUserLiveData.setValue(listUser);
    }

    public MutableLiveData<List<UserModel>> getListUserLiveData() {
        return listUserLiveData;
    }

    public boolean addUser(String categoryName, String categoryDesciption) {
//
//        for (CategoryModel category : MainActivity.categoryExpanses) {
//            if (category.getName().toLowerCase().equals(categoryName.toLowerCase())) {
//                return false;
//            }
//        }
//
//        int categoryId = listCategory.get(listCategory.size() - 1).getCategoryId() + 1;
//        CategoryModel category = new CategoryModel(categoryId, categoryName, categoryDesciption);
//        listCategory.add(category);
//        listCategoryLiveData.setValue(listCategory);
//        MainActivity.categoryExpanses.add(category);
        //thêm dữ liệu vào database
        return true;
    }

    public void deleteCategory(int position) {
//        CategoryModel category = listCategory.get(position);
//        listCategory.remove(position);
//        listCategoryLiveData.setValue(listCategory);
//        MainActivity.categoryExpanses.remove(position);
        //xóa dữ liệu trong database
    }

    public void updateCategory(int categoryId, String categoryName, String categoryDesciption) {
//        for (CategoryModel income : listCategory) {
//            if (income.getCategoryId() == categoryId) {
//                income.setName(categoryName);
//                income.setDescription(categoryDesciption);
//                break;
//            }
//        }
//        listCategoryLiveData.setValue(listCategory);
//        MainActivity.categoryExpanses.clear();
//        MainActivity.categoryExpanses.addAll(listCategory);
        //cập nhật dữ liệu xuống database
    }

}