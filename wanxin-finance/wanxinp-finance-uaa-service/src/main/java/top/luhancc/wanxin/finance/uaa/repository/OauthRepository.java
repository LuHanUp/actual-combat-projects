package top.luhancc.wanxin.finance.uaa.repository;


import top.luhancc.wanxin.finance.uaa.domain.OauthClientDetails;

import java.util.List;

public interface OauthRepository {

    OauthClientDetails findOauthClientDetails(String clientId);

    List<OauthClientDetails> findAllOauthClientDetails();

    void updateOauthClientDetailsArchive(String clientId, boolean archive);

    void saveOauthClientDetails(OauthClientDetails clientDetails);

    
}