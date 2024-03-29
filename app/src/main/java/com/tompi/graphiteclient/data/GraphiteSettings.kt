package com.tompi.graphiteclient.data

object GraphiteSettings {
    private val targetMap: MutableMap<String, GraphiteSettingItem> by lazy { loadFromPreferences() }

    fun addItem(item: GraphiteSettingItem) {
        targetMap.put(System.currentTimeMillis().toString(), item)
        saveToPreferences(targetMap)
    }

    fun removeItem(key: String): GraphiteSettingItem? {
        val ret = targetMap.remove(key)
        saveToPreferences(targetMap)
        return ret
    }

    fun replaceItem(key: String, value: GraphiteSettingItem) {
        targetMap.remove(key)
        targetMap.put(key, value)
        saveToPreferences(targetMap)
    }

    fun getSettingsByID(settingId: String): GraphiteSettingItem? {
        return targetMap.get(settingId)
    }

    private fun saveToPreferences(targetMap: MutableMap<String, GraphiteSettingItem>) {

    }

    fun getMap(): Map<String, GraphiteSettingItem> = targetMap.toMap()

    private fun loadFromPreferences(): MutableMap<String, GraphiteSettingItem> {
        val mock = mutableMapOf<String, GraphiteSettingItem>()
        //        val url = "https://play.grafana.org/api/datasources/proxy/1/render?target=summarize(apps.fakesite.web_server_01.counters.requests.count,%271hour%27,%27last%27)&from=-1h&format=json"
//        val url = "https://play.grafana.org/api/datasources/proxy/1/render?target=summarize(apps.fakesite.*.counters.requests.count,%271hour%27,%27last%27)&from=-1h&format=json"
//    val server = "https://play.grafana.org/api/datasources/proxy/1/"
//    val server = "http://192.168.1.164:8013/"
        val server2 = "192.168.1.164"
        val server = "tompi.synology.me"
        val port = "8013"
//    val target = "apps.fakesite.*.counters.requests.count"
        val target = "tompi.home.*.temperature"
        mock.put("1234", GraphiteSettingItem(
            listOf(GenericServer(false, server, port))
            , target))

        mock.put("12345", GraphiteSettingItem(
            listOf(GenericServer(false, server2, port), GenericServer(false, server, port))
            , target))
        return mock
    }
}

data class GraphiteServer(val isSecure: Boolean, val server: String, val port: String?, val target: String) {
    val url:String
        get() {
            val scheme = if(isSecure) "https" else "http"
            val portStr: String = if(port == null) "" else ":$port"
            return String.format("${scheme}://${server}${portStr}/render?target=summarize(${target},'1hour','last')&from=-1h&format=json")
        }
}

data class GraphiteSettingItem(private val serverList: List<GenericServer>, val target: String) {
    val targetIdx = target.split(".").indexOf("*")
    val servers: MutableList<GraphiteServer>
    init {
        val tmp = mutableListOf<GraphiteServer>()
        serverList.forEach {
            tmp.add(GraphiteServer(it.isSecure, it.server, it.port, target))
        }
        servers = tmp
    }
}



data class GenericServer(val isSecure: Boolean, val server: String, val port: String?) {
//    val url:String
//    get() {
//        val scheme = if(isSecure) "https" else "http"
//        val portStr: String = if(port == null) "" else ":$port"
//        return String.format("${scheme}://${server}${portStr}")
//    }
}

