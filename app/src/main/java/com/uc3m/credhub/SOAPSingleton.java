package com.uc3m.credhub;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

class SOAPSingleton
{
    private static HttpTransportSE androidHttpTransport;
    private static List<HeaderProperty> headerList_basicAuth;
    private static final String WS_NAMESPACE = "http://sdm_webrepo/";
    private static final String WS_METHOD_LIST = "ListCredentials";
    private static final String WS_METHOD_IMPORT = "ImportRecord";
    private static final String WS_METHOD_EXPORT = "ExportRecord";
    SoapSerializationEnvelope envelope;
    SoapObject request;
    ArrayList<PasswordEntity> passwordList;

    boolean USE_HTTPS;
    boolean USE_BASIC_AUTH;
    String BASIC_AUTH_USERNAME = "sdm";
    String BASIC_AUTH_PASSWORD = "repo4droid";
    URL urlWebService;


    // static variable single_instance of type Singleton
    private static SOAPSingleton single_instance = null;

    // variable of type String
    public String s;

    // private constructor restricted to this class itself
    private SOAPSingleton()
    {
        s = "Hello I am a string part of Singleton class";
    }

    // static method to create instance of Singleton class
    public static SOAPSingleton getInstance()
    {
        if (single_instance == null) {
            single_instance = new SOAPSingleton();
            single_instance.envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            single_instance.USE_HTTPS = false;
            single_instance.USE_BASIC_AUTH = false;
            single_instance.androidHttpTransport = new HttpTransportSE("http://10.0.2.2/SDM/WebRepo?wsdl");
            single_instance.passwordList = new ArrayList<>();
        }

        return single_instance;
    }

    public void exportRecord(final String id, final String username, final String password) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try
                {
                    // Export new record
                    System.out.println(id);
                    System.out.println(username);
                    System.out.println(password);

                    request = new SoapObject(WS_NAMESPACE, WS_METHOD_EXPORT);

                    PropertyInfo propId = new PropertyInfo();
                    propId.name = "arg0"; propId.setValue(id); propId.type = PropertyInfo.STRING_CLASS;
                    request.addProperty(propId);
                    PropertyInfo propUser = new PropertyInfo();
                    propUser.name = "arg1"; propUser.setValue(username); propUser.type = PropertyInfo.STRING_CLASS;
                    request.addProperty(propUser);
                    PropertyInfo propPass = new PropertyInfo();
                    propPass.name = "arg2"; propPass.setValue(password); propPass.type = PropertyInfo.STRING_CLASS;
                    request.addProperty(propPass);

                    envelope.setOutputSoapObject(request);
                    androidHttpTransport.call("\"" + WS_NAMESPACE + WS_METHOD_EXPORT + "\"", envelope, headerList_basicAuth);
                    System.out.println("Export result: " + envelope.getResponse().toString());
                    System.out.println();

                }
                catch(Exception ex)
                {
                    System.out.println("ERROR - " + ex.toString());
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public ArrayList<PasswordEntity> importData() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try
                {

                    // Read list of all record identifiers stored on the repository
                    request = new SoapObject(WS_NAMESPACE, WS_METHOD_LIST);
                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    androidHttpTransport.call("\"" + WS_NAMESPACE + WS_METHOD_LIST + "\"", envelope, headerList_basicAuth);
                    Vector<SoapPrimitive> listIds = new Vector<SoapPrimitive>();
                    if(envelope.getResponse() instanceof Vector) // 2+ elements
                        listIds.addAll((Vector<SoapPrimitive>) envelope.getResponse());
                    else if(envelope.getResponse() instanceof SoapPrimitive) // 1 element
                        listIds.add((SoapPrimitive)envelope.getResponse());
                    //System.out.println("List of records stored on the repo: ");
                    passwordList.clear();

                    for(int i = 0; i < listIds.size(); i++)
                    {
                        if (listIds.size() > 0)
                        {
                            PropertyInfo propId;
                            request = new SoapObject(WS_NAMESPACE, WS_METHOD_IMPORT);
                            propId = new PropertyInfo();
                            propId.name = "arg0"; propId.setValue(listIds.get(i).toString()); propId.type = PropertyInfo.STRING_CLASS;
                            request.addProperty(propId);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport.call("\"" + WS_NAMESPACE + WS_METHOD_IMPORT + "\"", envelope, headerList_basicAuth);

                            Vector<SoapPrimitive> importedRecord = (Vector<SoapPrimitive>)envelope.getResponse();
                            if(importedRecord.size() == 3)
                            {
                                System.out.println("Record imported successfully: ");
/*                                System.out.println("ID: " + importedRecord.get(0));
                                System.out.println("Username: " + importedRecord.get(1));
                                System.out.println("Password: " + importedRecord.get(2));*/
                                PasswordEntity entity = new PasswordEntity(importedRecord.get(0).toString(), "description", importedRecord.get(1).toString(), importedRecord.get(2).toString());
                                passwordList.add(entity);
                            }
                            else
                                System.out.println("Import error - " + importedRecord.get(0));
                        }
                        else System.out.println("Import aborted - No records found on the repo");
                        System.out.println("- " + listIds.get(i).toString());
                    }
                    return;

                }
                catch(Exception ex)
                {
                    System.out.println("ERROR - " + ex.toString());
                }
                return;
            }
        });

        thread.start();
        try {
            thread.join();
            return passwordList;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }
}
