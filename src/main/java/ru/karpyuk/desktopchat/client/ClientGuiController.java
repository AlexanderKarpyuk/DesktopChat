package ru.karpyuk.desktopchat.client;

public class ClientGuiController extends Client {
    private ClientGuiModel model = new ClientGuiModel();
    private ClientGuiView view = new ClientGuiView(this);

    public class GuiSocketThread extends SocketThread {
        @Override
        protected void processIncomingMessage(String message) {
            model.setNewMessage(message);
            view.refreshMessages();
            //super.processIncomingMessage(message);
        }

        @Override
        protected void informAboutAddingNewUser(String userName) {
            model.addUser(userName);
            view.refreshUsers();
        }

        @Override
        protected void informAboutDeletingNewUser(String userName) {
            model.deleteUser(userName);
            view.refreshUsers();
        }

        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }

    public static void main(String[] args) {
        new ClientGuiController().run();
    }

    public ClientGuiModel getModel() {
        return model;
    }

    @Override
    public void run() {
        getSocketThread().run();
    }

    @Override
    protected String getServerAddress() {
        return view.getServerAddress();
    }

    @Override
    protected int getServerPort() {
        return view.getServerPort();
    }

    @Override
    protected String getUserName() {
        return view.getUserName();
    }

    @Override
    protected SocketThread getSocketThread() {
        return new GuiSocketThread();
    }
}