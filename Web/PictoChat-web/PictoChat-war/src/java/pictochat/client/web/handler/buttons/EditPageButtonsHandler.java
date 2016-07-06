package pictochat.client.web.handler.buttons;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.util.image.ImageUploadResizer;
import kpoint.javaee.web.handler.forms.FormInputField;
import kpoint.javaee.web.navigation.AbstractNavigatingHandler;
import kpoint.javaee.web.resources.ResourceBundleWrapper;
import org.apache.log4j.Logger;
import org.richfaces.event.DropEvent;
import org.richfaces.event.UploadEvent;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;
import org.richfaces.model.UploadItem;
import pictochat.client.web.bundles.Bundles;
import pictochat.client.web.handler.AbstractCompoundEntityHandler;
import pictochat.client.web.handler.page.fdto.PageButtonFDTO;
import pictochat.client.web.handler.page.fdto.PageButtonRowFDTO;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.client.web.orlando.forms.AbstractFormHandler;
import pictochat.server.dal.local.IButtonImageDAOLocal;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.dal.local.IPageButtonDAOLocal;
import pictochat.server.dal.local.IPageDAOLocal;
import pictochat.server.persistence.ButtonImage;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Page;
import pictochat.server.persistence.PageButton;
import pictochat.server.persistence.enums.Action;
import pictochat.server.persistence.enums.Icon;
import static pictochat.server.persistence.enums.Icon.Beta;
import static pictochat.server.persistence.enums.Icon.Sclera;
import static pictochat.server.persistence.enums.Icon.Upload;

/**
 *
 * @author Steven
 */
public class EditPageButtonsHandler extends AbstractFormHandler
{
    private static final Logger LOG = Logger.getLogger(EditPageButtonsHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;
    private ButtonsHandler buttonsHandler = new ButtonsHandler();
    private ImageUploadResizer imageUploadResizer = new ImageUploadResizer(64, 64, new String[]{"jpeg", "jpg", "png", "gif"});

    private static final String BETA_URL = "http://webservices.ccl.kuleuven.be/picto/beta/";
    private static final String SCLERA_URL = "http://webservices.ccl.kuleuven.be/picto/sclera/";
    private static final String ABLE2INCLUDE_URL = "http://al.abletoinclude.eu/Text2Picto.php";


    private Client activeClient;
    private Page activePage;
    private ButtonImage image;
    private ActiveButtonManager activeButtonManager = new ActiveButtonManager();
    private List<String> list = new ArrayList<String>();

    private List<PageButtonRowFDTO> rows = null;


    public Client getActiveClient() {
        return activeClient;
    }
    public void setActiveClient(Client activeClient) {
        try {
            this.activeClient = this.clientDAO.findClientByIdWithPages(activeClient.getId());
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }
    }

    public Page getActivePage() {
        return activePage;
    }
    public void setActivePage(Page activePage) {
        this.invalidate();
        this.activeButtonManager.clear();
        this.activePage = activePage;
    }

    public PageButton getActiveButton() {
        this.getFindAllButtonRowsForCurrentPage();

        return activeButtonManager.getActiveButton();
    }
    public Integer getActiveCell() {
        return activeButtonManager.getActiveCell();
    }


    public Long getTargetPageId() {
        PageButton activeButton = this.activeButtonManager.getActiveButton();
        if (activeButton != null && activeButton.getTargetPage() != null)
            return activeButton.getTargetPage().getId();

        return null;
    }
    public void setTargetPageId(Long pageId) {
        for (Page p : this.activeClient.getPages())
            if (p.getId().equals(pageId))
                this.activeButtonManager.getActiveButton().setTargetPage(p);
    }


    public Boolean getShowImageUpload() {
        PageButton activeButton = this.activeButtonManager.getActiveButton();
        return activeButton != null && Icon.Upload.equals(activeButton.getIcon());
    }
    public Boolean getShowImageName() {
        PageButton activeButton = this.activeButtonManager.getActiveButton();
        return activeButton != null && (Icon.Beta.equals(activeButton.getIcon()) || Icon.Sclera.equals(activeButton.getIcon()));
    }
    public Boolean getShowPageDropdown() {
        PageButton activeButton = this.activeButtonManager.getActiveButton();
        return activeButton != null && (Action.Navigate.equals(activeButton.getAction()) || Action.NavigateAndText.equals(activeButton.getAction()));
    }
    public Boolean getShowTextInput() {
        PageButton activeButton = this.activeButtonManager.getActiveButton();
        return activeButton != null && (Action.Text.equals(activeButton.getAction()) || Action.NavigateAndText.equals(activeButton.getAction()));
    }


    public List<PageButtonRowFDTO> getFindAllButtonRowsForCurrentPage() {
        if (this.rows != null)
            return this.rows;

        this.rows = new ArrayList<PageButtonRowFDTO>();

        Integer rowIndex = 0;
        int columnCount = activePage.getColumns();

        List<PageButtonFDTO> buttons = new ArrayList<PageButtonFDTO>();
        for (PageButtonFDTO button : buttonsHandler.getFindAllButtonsForCurrentPage()) {
            if (button.getCell().equals(activeButtonManager.getActiveCell()))
                buttons.add(buttonsHandler.convertToFormModel(activeButtonManager.getActiveButton()));
            else
                buttons.add(button);

            if (buttons.size() == columnCount) {
                this.rows.add(new PageButtonRowFDTO(rowIndex++, buttons));
                buttons = new ArrayList<PageButtonFDTO>();
            }
        }

        //Add remaining row of buttons to list
        if (!buttons.isEmpty())
            this.rows.add(new PageButtonRowFDTO(rowIndex++, buttons));

        /* Select the first cell when none are selected.
         * Create a default one if the first cell doesn't exist yet. */
        if (this.activeButtonManager.getActiveButton() == null) {
            PageButton activeButton = buttonsHandler.getDataModelFromKey(1);
            if (activeButton.getId() != null)
                this.activeButtonManager.setActiveButton((PageButton)activeButton.clone(), 1);
            else {
                PageButton pb = PageButton.buildDefault(1);
                this.activeButtonManager.setActiveButton(pb, 1);
            }
        }

        return this.rows;
    }


    public String show() {
        this.setActiveClient(this.activeClient);

        FacesContext ctx = FacesContext.getCurrentInstance();
        Long pageId = Long.parseLong(ctx.getExternalContext().getRequestParameterMap().get("page"));
        if (this.pageDAO != null && pageId != null)
            return load(pageId);

        return null;
    }
    public void close() {
        this.setActivePage(null);
    }

    private String load(Long pageId) {
        this.list.clear();

        try {
            LOG.debug("load page: " + pageId);
            this.setActivePage(this.pageDAO.findPageById(pageId));

            return this.navigatingHandler.getNavigateTo().getSecure().getPage().getButtons().getEditAction();
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }

        return null;
    }
    private void invalidate() {
        this.rows = null;
        this.buttonsHandler.clearModels();
    }


    public void translateTextToPictos() {
        this.list.clear();

        try {
            String text = this.activeButtonManager.getActiveButton().getUrl();
            if (text != null) {
                text = URLEncoder.encode(text, "UTF-8");

                FacesContext ctx = FacesContext.getCurrentInstance();
                Locale locale = ctx.getViewRoot().getLocale();

                StringBuilder sb = new StringBuilder();
                sb
                    .append("?text=").append(text)
                    .append("&type=").append(this.activeButtonManager.getActiveButton().getIcon().name().toLowerCase())
                    .append("&language=").append(locale.getDisplayLanguage(Locale.ENGLISH).toLowerCase());

                LOG.debug(ABLE2INCLUDE_URL + sb.toString());
                URL url = new URL(ABLE2INCLUDE_URL + sb.toString());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);

                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                if (!jsonResponse.isNull("pictos")) {
                    JSONArray jsonPictos = jsonResponse.getJSONArray("pictos");
                    for (int i = 0; i < jsonPictos.length(); i++) {
                        String value = jsonPictos.getString(i).trim();
                        LOG.debug(value);

                        if (!value.isEmpty())
                            this.list.add(jsonPictos.getString(i));
                    }
                }
            }
        } catch (MalformedURLException ex) {
            LOG.error(null, ex);
        } catch (IOException ex) {
            LOG.error(null, ex);
        } catch (JSONException ex) {
            LOG.error(null, ex);
        }
    }
    public List<String> getResultPictos() {
        return this.list;
    }
    public void selectPicto() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        String url = ctx.getExternalContext().getRequestParameterMap().get("url");

        url = url.replace(BETA_URL, "");
        url = url.replace(SCLERA_URL, "");
        url = url.replace("/", "");

        LOG.debug("SelectPicto: " + url);
        this.activeButtonManager.getActiveButton().setUrl(url);

        invalidate();
    }


    public void select() {
        this.activeButtonManager.clear();
        this.list.clear();

        FacesContext ctx = FacesContext.getCurrentInstance();
        String cellString = ctx.getExternalContext().getRequestParameterMap().get("cell");
        Integer cellId = Integer.parseInt(cellString);

        if (cellId != null) {
            String s = "Select cell: " + cellId;

            PageButton button = buttonsHandler.getDataModelFromKey(cellId);
            if (button.getId() != null) {
                s += " - existing button: " + button.getId();
                this.activeButtonManager.setActiveButton((PageButton)button.clone(), cellId);
            }
            else {
                s += " - new default button";
                button = PageButton.buildDefault(cellId);
                this.activeButtonManager.setActiveButton(button, cellId);
            }

            LOG.debug(s);

            invalidate();
        }
    }
    public void save() {
        PageButton activeButton = this.activeButtonManager.getActiveButton();

        LOG.debug("Save: " + activeButton.getId() + " - " + this.activePage.getButtons().contains(activeButton));

        if (this.image != null)
            activeButton.setImage(image);

        if (this.activePage.getButtons().contains(activeButton)) {
            //Remove the original and add the clone
            this.activePage.getButtons().remove(activeButton);
            this.activePage.getButtons().add(activeButton);
        }
        else {
            this.activePage.getButtons().add(activeButton);
        }

        try {
            this.pageDAO.update(this.activePage);

            invalidate();

            activeButtonManager.setActiveButton((PageButton)activeButton.clone(), activeButton.getCell());
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }
    }
    public void delete() {
        PageButton activeButton = activeButtonManager.getActiveButton();
        if (activeButton != null) {
            try {
                activePage.getButtons().remove(activeButton);

                pageDAO.update(activePage);
                pageButtonDAO.destroy(activeButton);
            } catch (InvalidParameterException ex) {
                LOG.error(null, ex);
            } catch (EntityNotFoundException ex) {
                LOG.error(null, ex);
            } catch (StorageException ex) {
                LOG.error(null, ex);
            }
        }

        activeButtonManager.clear();
        this.list.clear();

        invalidate();
    }

    public List<SelectItem> getActions() {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();

        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.ENUM_ACTION);
        for (Action a : Action.values())
            selectItems.add(new SelectItem(a, resourceBundle.getString(a.name().toLowerCase())));

        return selectItems;
    }
    public List<SelectItem> getIcons() {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();

        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.ENUM_ICON);
        for (Icon i : Icon.values())
            selectItems.add(new SelectItem(i, resourceBundle.getString(i.name().toLowerCase())));

        return selectItems;
    }
    public List<SelectItem> getPages() {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();

        for (Page p : this.activeClient.getPages())
            if (!p.getId().equals(this.activePage.getId()))
                selectItems.add(new SelectItem(p.getId(), p.getName()));

        return selectItems;
    }


    public void actionChanged(ValueChangeEvent event) {
        this.list.clear();
        invalidate();

        Object o = event.getNewValue();
        if (o instanceof Action) {
            Action action = (Action)o;
            PageButton activeButton = this.activeButtonManager.getActiveButton();
            activeButton.setAction(action);
        }
    }
    public void iconChanged(ValueChangeEvent event) {
        invalidate();

        Object o = event.getNewValue();
        if (o instanceof Icon)
            this.activeButtonManager.getActiveButton().setIcon((Icon)o);
    }

    public void processDrop(DropEvent event) {
        PageButtonFDTO droppedOn = (PageButtonFDTO)event.getDropValue();
        PageButtonFDTO dragged = (PageButtonFDTO)event.getDragValue();

        LOG.info(String.format("Dropped %s on %s", dragged.getCell(), droppedOn.getCell()));

        Integer sourceCell = dragged.getCell();
        Integer targetCell = droppedOn.getCell();

        PageButton droppedOnButton = buttonsHandler.getDataModelFromKey(targetCell);
        PageButton draggedButton = buttonsHandler.getDataModelFromKey(sourceCell);

        try {
            //We can't just swap these because of the unique constraint
            droppedOnButton.setCell(999);
            pageButtonDAO.update(droppedOnButton);
            draggedButton.setCell(targetCell);
            pageButtonDAO.update(draggedButton);
            droppedOnButton.setCell(sourceCell);
            pageButtonDAO.update(droppedOnButton);

            //Preserve UI consistency by updating the working copy
            Integer activeCell = activeButtonManager.getActiveCell();
            if (sourceCell.equals(activeCell))
            {
                PageButton activeButton = activeButtonManager.getActiveButton();
                activeButton.setCell(targetCell);
                activeButtonManager.setActiveButton(activeButton, activeButton.getCell());
            }
            else if (targetCell.equals(activeCell))
            {
                PageButton activeButton = activeButtonManager.getActiveButton();
                activeButton.setCell(sourceCell);
                activeButtonManager.setActiveButton(activeButton, activeButton.getCell());
            }

            this.invalidate();
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }
    }


    @PostConstruct
    public void postConstruct() {
        this.navigatingHandler = new AbstractNavigatingHandler(){
            @Override
            public Logger getLogger() {
                return LOG;
            }

            @Override
            public MethodLogger getMethodLogger() {
                return METHOD_LOG;
            }
        };
        this.navigatingHandler.postConstruct();
    }


    public void uploadListener(UploadEvent event) throws Exception {
        if (getButtonImageDAO() != null) {
            UploadItem item = event.getUploadItem();

            byte[] imageData = imageUploadResizer.resize(item);
            if (imageData != null) {
                try {
                    if (this.image != null) {
                        try { this.buttonImageDAO.destroy(this.image); }
                        catch (Exception ex) {}
                    }

                    this.image = this.buttonImageDAO.create(imageData, item.getContentType());
                    this.activeButtonManager.getActiveButton().setImage(image);
                } catch (InvalidParameterException ex) {
                    LOG.error(null, ex);
                } catch (StorageException ex) {
                    LOG.error(null, ex);
                }
            }
        }
    }


    private class ButtonsHandler extends AbstractCompoundEntityHandler<PageButton, PageButtonFDTO, Integer> {
        private Map<Integer, PageButton> map = new HashMap<Integer, PageButton>();

        public List<PageButtonFDTO> getFindAllButtonsForCurrentPage() {
            if (!this.isInit()) {
                LOG.debug("ClearModels");
                this.clearModels();

                map.clear();
                if (activePage != null) {
                    int rowCount = activePage.getRows();
                    int columnCount = activePage.getColumns();

                    for (PageButton b : activePage.getButtons()) {
                        if (!map.containsKey(b.getCell()))
                            map.put(b.getCell(), b);
                    }

                    for (int i = 1; i <= rowCount * columnCount; i++) {
                        PageButton button;

                        //Use the one in the map
                        if (map.containsKey(i))
                            button = map.get(i);
                        //Otherwise make a dummy to send the cell as a parameter if no button is found
                        else
                            button = PageButton.buildDefault(i);

                        addModel(button);
                    }

                    finish();
                }
            }

            return this.getFormModels();
        }
        public void setFindAllButtonsForCurrentPage(List<PageButtonFDTO> data) {}

        @Override
        public PageButtonFDTO convertToFormModel(PageButton model) {
            int columnCount = activePage.getColumns();
            Integer cell = convertDataModelToKey(model).intValue();

            String url = null;
            Icon icon = model.getIcon();
            if (icon != null) {
                switch (icon) {
                    case Beta:
                        url = "background-image: url(" + BETA_URL + model.getUrl() + ");";
                        break;
                    case Sclera:
                        url = "background-image: url(" + SCLERA_URL + model.getUrl() + ");";
                        break;
                    case Upload:
                        ButtonImage image = model.getImage();
                        if (image != null)
                            url = image.getBackgroundImageStyle();
                        break;
                }
            }

            return new PageButtonFDTO(model.getId(), cell, model.getColor(), url, this.getColumnStyleForColumnCount(columnCount));
        }
        @Override
        public Integer convertDataModelToKey(PageButton model) {
            return model.getCell();
        }

        @Override
        public List<PageButtonFDTO> getData() {
            return this.getFindAllButtonsForCurrentPage();
        }
        @Override
        public void setData(List<PageButtonFDTO> data) {
            this.setFindAllButtonsForCurrentPage(data);
        }

        private String getColumnStyleForColumnCount(int columns) {
            return "col-sm-" + (12 / columns);
        }
    }



    //Form Messages
    @Override
    public List<FormInputField> getAllMessages() {
        List<FormInputField> list = new ArrayList<FormInputField>();

        list.add(this.getImageMessages());

        return list;
    }

    public FormInputField getImageMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.EDIT_BUTTONS);
        return this.getFormInputField(resourceBundle.getString("image"), "edit_form:avatar");
    }


    private class ActiveButtonManager {
        private PageButton activeButton;
        private Integer activeCell;

        public PageButton getActiveButton() {
            return activeButton;
        }
        public Integer getActiveCell() {
            return activeCell;
        }

        public void setActiveButton(PageButton button, Integer cell) {
            this.activeButton = button;
            this.activeCell = cell;
        }

        public void clear() {
            this.activeButton = null;
            this.activeCell = null;
        }
    }





    //DAO Objects
    private IClientDAOLocal clientDAO;
    private IPageDAOLocal pageDAO;
    private IPageButtonDAOLocal pageButtonDAO;
    private IButtonImageDAOLocal buttonImageDAO;

    public IClientDAOLocal getClientDAO() {
        return clientDAO;
    }
    public void setClientDAO(IClientDAOLocal clientDAO) {
        this.clientDAO = clientDAO;
    }

    public IPageDAOLocal getPageDAO() {
        return pageDAO;
    }
    public void setPageDAO(IPageDAOLocal pageDAO) {
        this.pageDAO = pageDAO;
    }

    public IPageButtonDAOLocal getPageButtonDAO() {
        return pageButtonDAO;
    }
    public void setPageButtonDAO(IPageButtonDAOLocal pageButtonDAO) {
        this.pageButtonDAO = pageButtonDAO;
    }

    public IButtonImageDAOLocal getButtonImageDAO() {
        return buttonImageDAO;
    }
    public void setButtonImageDAO(IButtonImageDAOLocal buttonImageDAO) {
        this.buttonImageDAO = buttonImageDAO;
    }
}