/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\workSpace\\VideoTV_huaWei\\TVClient\\src\\com\\neteast\\longtv\\aidl\\ISearchTV.aidl
 */
package com.neteast.longtv.aidl;
/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-4-23
 */
public interface ISearchTV extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.neteast.longtv.aidl.ISearchTV
{
private static final java.lang.String DESCRIPTOR = "com.neteast.longtv.aidl.ISearchTV";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.neteast.longtv.aidl.ISearchTV interface,
 * generating a proxy if needed.
 */
public static com.neteast.longtv.aidl.ISearchTV asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.neteast.longtv.aidl.ISearchTV))) {
return ((com.neteast.longtv.aidl.ISearchTV)iin);
}
return new com.neteast.longtv.aidl.ISearchTV.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_queryName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.queryName(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.neteast.longtv.aidl.ISearchTV
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public java.lang.String queryName(java.lang.String videoName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(videoName);
mRemote.transact(Stub.TRANSACTION_queryName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_queryName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public java.lang.String queryName(java.lang.String videoName) throws android.os.RemoteException;
}
