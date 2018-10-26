friendRequestDataBase.child(current_user.getUid())
                            .child(user_id).child("request_type")
                            .setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        friendRequestDataBase.child(user_id).child(current_user.getUid()).child("request_type")
                                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @SuppressLint("ResourceAsColor")
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                HashMap<String ,String > notificationData = new HashMap<>();
                                                notificationData.put("from",current_user.getUid());
                                                notificationData.put("type","request");

                                                notificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        RequestSend.setEnabled(true);
                                                        current_state = "req_sent";
                                                        RequestSend.setText("Cancel Friend Request");
                                                        RequestCancel.setVisibility(View.INVISIBLE);
                                                        RequestCancel.setEnabled(false);
                                                    }
                                                });
                                            }
                                        });

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });