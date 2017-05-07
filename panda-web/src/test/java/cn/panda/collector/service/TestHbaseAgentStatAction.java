package cn.panda.collector.service;

import cn.panda.common.server.bo.stat.ActiveTraceBo;
import cn.panda.web.dao.hbase.stat.v2.HbaseActiveTraceDaoV2;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-web.xml")
@WebAppConfiguration
public class TestHbaseAgentStatAction {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
//        ClassPathXmlApplicationContext context =
//                new ClassPathXmlApplicationContext("classpath:applicationContext-web.xml");
//        context.start();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void testSelectHbaseAgentStatList() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/getAgentStat/activeTrace"))
//                .andExpect(MockMvcResultMatchers.view().name("/activeTrace"))
//                .andReturn();
//        mockMvc.perform(MockMvcRequestBuilders.get("/getAgentStat/activeTrace"));
        mockMvc.perform(MockMvcRequestBuilders.get("/getAgentStat/activeTrace?agentId=1&from=1l&to=2l"));

    }

    @Test
    public void testInsertActiveTrace() {
//        ClassPathXmlApplicationContext context =
//                new ClassPathXmlApplicationContext("classpath:applicationContext-web.xml");
//        context.start();
        HbaseActiveTraceDaoV2 agentStatDaoV2 = new HbaseActiveTraceDaoV2();
        List<ActiveTraceBo> traceBos = new ArrayList<>();
        ActiveTraceBo traceBo = new ActiveTraceBo();
        traceBo.setAgentId("00001");
        traceBo.setHistogramSchemaType(3);
        traceBo.setTimestamp(System.currentTimeMillis());
        traceBo.setVersion((short) 3);
        traceBos.add(traceBo);
        agentStatDaoV2.insert("00001", traceBos);

    }

}
