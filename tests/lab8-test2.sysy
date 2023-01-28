int sort_arr[5];
int combine(int arr1[], int arr1_length, int arr2[], int arr2_length) {
    int i = 0;
    int j = 0;
    int k = 0;
    while (i < arr1_length && j < arr2_length) {
        if (arr1[i] < arr2[j]) {
            sort_arr[k] = arr1[i];
            i = i + 1;
        }
        else {
            sort_arr[k] = arr2[j];
            j = j + 1;
        }
        k = k + 1;
    }
    if (i == arr1_length) {
        while (j < arr2_length) {
            sort_arr[k] = arr2[j];
            k = k + 1;
            j = j + 1;
        }
    }
    else {
        while (i < arr1_length) {
            sort_arr[k] = arr2[i];
            k = k + 1;
            i = i + 1;
        }
    }
    return sort_arr[arr1_length + arr2_length - 1];
}

int main() {
    int a[2] = { 1,5 };
    int b[3] = { 1,4,14 };
    return combine(a, 2, b, 3);
}
