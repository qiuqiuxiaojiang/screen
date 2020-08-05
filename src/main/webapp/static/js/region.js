var provinceList = [
{name:'北京', cityList:[
{name:'辖', areaList:['东城','西城','崇文','宣武','朝阳','丰台','石景山','海淀','门头沟','房山','通州','顺义','昌平','大兴','怀柔','平谷','密云','延庆']}
]},
{name:'上海', cityList:[
{name:'辖', areaList:['黄浦','卢湾','徐汇','长宁','静安','普陀','闸北','虹口','杨浦','闵行','宝山','嘉定','浦东新','金山','松江','青浦','南汇','奉贤','崇明']}
]},
{name:'深圳', cityList:[
{name:'辖', areaList:['罗湖','龙岗','龙华','福田','宝安','南山','盐田','坪山']}
]},
{name:'天津', cityList:[
{name:'辖', areaList:['和平','河东','河西','南开','河北','红桥','塘沽','汉沽','大港','东丽','西青','津南','北辰','武清','宝坻','宁河','静海','蓟　']}
]},
{name:'重庆', cityList:[
{name:'辖', areaList:['万州','涪陵','渝中','大渡口','江北','沙坪坝','九龙坡','南岸','北碚','万盛','双桥','渝北','巴南','黔江','长寿']},
{name:'县', areaList:['綦江县','潼南县','铜梁县','大足县','荣昌县','璧山县','梁平县','城口县','丰都县','垫江县','武隆县','忠　县','开　县','云阳县','奉节县','巫山县','巫溪县','石柱土家族自治县','秀山土家族苗族自治县','酉阳土家族苗族自治县','彭水苗族土家族自治县']},
{name:'', areaList:['江津','合川','永川','南川']}
]},
{name:'四川', cityList:[
{name:'成都', areaList:['辖','锦江','青羊','金牛','武侯','成华','龙泉驿','青白江','新都','温江县','金堂县','双流县','郫　县','大邑县','蒲江县','新津县','都江堰','彭州','邛崃','崇州']},
{name:'自贡', areaList:['辖','自流井','贡井','大安','沿滩','荣　县','富顺县']},
{name:'攀枝花', areaList:['辖','东　','西　','仁和','米易县','盐边县']},
{name:'泸州', areaList:['辖','江阳','纳溪','龙马潭','泸　县','合江县','叙永县','古蔺县']},
{name:'德阳', areaList:['辖','旌阳','中江县','罗江县','广汉','什邡','绵竹']},
{name:'绵阳', areaList:['辖','涪城','游仙','三台县','盐亭县','安　县','梓潼县','北川羌族自治县','平武县','江油']},
{name:'广元', areaList:['辖','中','元坝','朝天','旺苍县','青川县','剑阁县','苍溪县']},
{name:'遂宁', areaList:['辖','船山','安居','蓬溪县','射洪县','大英县']},
{name:'内江', areaList:['辖','中','东兴','威远县','资中县','隆昌县']},
{name:'乐山', areaList:['辖','中','沙湾','五通桥','金口河','犍为县','井研县','夹江县','沐川县','峨边彝族自治县','马边彝族自治县','峨眉山']},
{name:'南充', areaList:['辖','顺庆','高坪','嘉陵','南部县','营山县','蓬安县','仪陇县','西充县','阆中']},
{name:'眉山', areaList:['辖','东坡','仁寿县','彭山县','洪雅县','丹棱县','青神县']},
{name:'宜宾', areaList:['辖','翠屏','宜宾县','南溪县','江安县','长宁县','高　县','珙　县','筠连县','兴文县','屏山县']},
{name:'广安', areaList:['辖','广安','岳池县','武胜县','邻水县','华莹']},
{name:'达州', areaList:['辖','通川','达　县','宣汉县','开江县','大竹县','渠　县','万源']},
{name:'雅安', areaList:['辖','雨城','名山县','荥经县','汉源县','石棉县','天全县','芦山县','宝兴县']},
{name:'巴中', areaList:['辖','巴州','通江县','南江县','平昌县']},
{name:'资阳', areaList:['辖','雁江','安岳县','乐至县','简阳']},
{name:'阿坝藏族羌族自治州', areaList:['汶川县','理　县','茂　县','松潘县','九寨沟县','金川县','小金县','黑水县','马尔康县','壤塘县','阿坝县','若尔盖县','红原县']},
{name:'甘孜藏族自治州', areaList:['康定县','泸定县','丹巴县','九龙县','雅江县','道孚县','炉霍县','甘孜县','新龙县','德格县','白玉县','石渠县','色达县','理塘县','巴塘县','乡城县','稻城县','得荣县']},
{name:'凉山彝族自治州', areaList:['西昌','木里藏族自治县','盐源县','德昌县','会理县','会东县','宁南县','普格县','布拖县','金阳县','昭觉县','喜德县','冕宁县','越西县','甘洛县','美姑县','雷波县']}
]},
{name:'贵州', cityList:[
{name:'贵阳', areaList:['辖','南明','云岩','花溪','乌当','白云','小河','开阳县','息烽县','修文县','清镇']},
{name:'六盘水', areaList:['钟山','六枝特','水城县','盘　县']},
{name:'遵义', areaList:['辖','红花岗','汇川','遵义县','桐梓县','绥阳县','正安县','道真仡佬族苗族自治县','务川仡佬族苗族自治县','凤冈县','湄潭县','余庆县','习水县','赤水','仁怀']},
{name:'安顺', areaList:['辖','西秀','平坝县','普定县','镇宁布依族苗族自治县','关岭布依族苗族自治县','紫云苗族布依族自治县']},
{name:'铜仁地', areaList:['铜仁','江口县','玉屏侗族自治县','石阡县','思南县','印江土家族苗族自治县','德江县','沿河土家族自治县','松桃苗族自治县','万山特']},
{name:'黔西南布依族苗族自治州', areaList:['兴义','兴仁县','普安县','晴隆县','贞丰县','望谟县','册亨县','安龙县']},
{name:'毕节地', areaList:['毕节','大方县','黔西县','金沙县','织金县','纳雍县','威宁彝族回族苗族自治县','赫章县']},
{name:'黔东南苗族侗族自治州', areaList:['凯里','黄平县','施秉县','三穗县','镇远县','岑巩县','天柱县','锦屏县','剑河县','台江县','黎平县','榕江县','从江县','雷山县','麻江县','丹寨县']},
{name:'黔南布依族苗族自治州', areaList:['都匀','福泉','荔波县','贵定县','瓮安县','独山县','平塘县','罗甸县','长顺县','龙里县','惠水县','三都水族自治县']}
]},
{name:'云南', cityList:[
{name:'昆明', areaList:['辖','五华','盘龙','官渡','西山','东川','呈贡县','晋宁县','富民县','宜良县','石林彝族自治县','嵩明县','禄劝彝族苗族自治县','寻甸回族彝族自治县','安宁']},
{name:'曲靖', areaList:['辖','麒麟','马龙县','陆良县','师宗县','罗平县','富源县','会泽县','沾益县','宣威']},
{name:'玉溪', areaList:['辖','红塔','江川县','澄江县','通海县','华宁县','易门县','峨山彝族自治县','新平彝族傣族自治县','元江哈尼族彝族傣族自治县']},
{name:'保山', areaList:['辖','隆阳','施甸县','腾冲县','龙陵县','昌宁县']},
{name:'昭通', areaList:['辖','昭阳','鲁甸县','巧家县','盐津县','大关县','永善县','绥江县','镇雄县','彝良县','威信县','水富县']},
{name:'丽江', areaList:['辖','古城','玉龙纳西族自治县','永胜县','华坪县','宁蒗彝族自治县']},
{name:'思茅', areaList:['辖','翠云','普洱哈尼族彝族自治县','墨江哈尼族自治县','景东彝族自治县','景谷傣族彝族自治县','镇沅彝族哈尼族拉祜族自治县','江城哈尼族彝族自治县','孟连傣族拉祜族佤族自治县','澜沧拉祜族自治县','西盟佤族自治县']},
{name:'临沧', areaList:['辖','临翔','凤庆县','云　县','永德县','镇康县','双江拉祜族佤族布朗族傣族自治县','耿马傣族佤族自治县','沧源佤族自治县']},
{name:'楚雄彝族自治州', areaList:['楚雄','双柏县','牟定县','南华县','姚安县','大姚县','永仁县','元谋县','武定县','禄丰县']},
{name:'红河哈尼族彝族自治州', areaList:['个旧','开远','蒙自县','屏边苗族自治县','建水县','石屏县','弥勒县','泸西县','元阳县','红河县','金平苗族瑶族傣族自治县','绿春县','河口瑶族自治县']},
{name:'文山壮族苗族自治州', areaList:['文山县','砚山县','西畴县','麻栗坡县','马关县','丘北县','广南县','富宁县']},
{name:'西双版纳傣族自治州', areaList:['景洪','勐海县','勐腊县']},
{name:'大理白族自治州', areaList:['大理','漾濞彝族自治县','祥云县','宾川县','弥渡县','南涧彝族自治县','巍山彝族回族自治县','永平县','云龙县','洱源县','剑川县','鹤庆县']},
{name:'德宏傣族景颇族自治州', areaList:['瑞丽','潞西','梁河县','盈江县','陇川县']},
{name:'怒江傈僳族自治州', areaList:['泸水县','福贡县','贡山独龙族怒族自治县','兰坪白族普米族自治县']},
{name:'迪庆藏族自治州', areaList:['香格里拉县','德钦县','维西傈僳族自治县']}
]},
{name:'西藏', cityList:[
{name:'拉萨', areaList:['辖','城关','林周县','当雄县','尼木县','曲水县','堆龙德庆县','达孜县','墨竹工卡县']},
{name:'昌都地', areaList:['昌都县','江达县','贡觉县','类乌齐县','丁青县','察雅县','八宿县','左贡县','芒康县','洛隆县','边坝县']},
{name:'山南地', areaList:['乃东县','扎囊县','贡嘎县','桑日县','琼结县','曲松县','措美县','洛扎县','加查县','隆子县','错那县','浪卡子县']},
{name:'日喀则地', areaList:['日喀则','南木林县','江孜县','定日县','萨迦县','拉孜县','昂仁县','谢通门县','白朗县','仁布县','康马县','定结县','仲巴县','亚东县','吉隆县','聂拉木县','萨嘎县','岗巴县']},
{name:'那曲地', areaList:['那曲县','嘉黎县','比如县','聂荣县','安多县','申扎县','索　县','班戈县','巴青县','尼玛县']},
{name:'阿里地', areaList:['普兰县','札达县','噶尔县','日土县','革吉县','改则县','措勤县']},
{name:'林芝地', areaList:['林芝县','工布江达县','米林县','墨脱县','波密县','察隅县','朗　县']}
]},
{name:'河南', cityList:[
{name:'郑州', areaList:['辖','中原','二七','管城回族','金水','上街','邙山','中牟县','巩义','荥阳','新密','新郑','登封']},
{name:'开封', areaList:['辖','龙亭','顺河回族','鼓楼','南关','郊　','杞　县','通许县','尉氏县','开封县','兰考县']},
{name:'洛阳', areaList:['辖','老城','西工','廛河回族','涧西','吉利','洛龙','孟津县','新安县','栾川县','嵩　县','汝阳县','宜阳县','洛宁县','伊川县','偃师']},
{name:'平顶山', areaList:['辖','新华','卫东','石龙','湛河','宝丰县','叶　县','鲁山县','郏　县','舞钢','汝州']},
{name:'安阳', areaList:['辖','文峰','北关','殷都','龙安','安阳县','汤阴县','滑　县','内黄县','林州']},
{name:'鹤壁', areaList:['辖','鹤山','山城','淇滨','浚　县','淇　县']},
{name:'新乡', areaList:['辖','红旗','卫滨','凤泉','牧野','新乡县','获嘉县','原阳县','延津县','封丘县','长垣县','卫辉','辉县']},
{name:'焦作', areaList:['辖','解放','中站','马村','山阳','修武县','博爱县','武陟县','温　县','济源','沁阳','孟州']},
{name:'濮阳', areaList:['辖','华龙','清丰县','南乐县','范　县','台前县','濮阳县']},
{name:'许昌', areaList:['辖','魏都','许昌县','鄢陵县','襄城县','禹州','长葛']},
{name:'漯河', areaList:['辖','源汇','郾城','召陵','舞阳县','临颍县']},
{name:'三门峡', areaList:['辖','湖滨','渑池县','陕　县','卢氏县','义马','灵宝']},
{name:'南阳', areaList:['辖','宛城','卧龙','南召县','方城县','西峡县','镇平县','内乡县','淅川县','社旗县','唐河县','新野县','桐柏县','邓州']},
{name:'商丘', areaList:['辖','梁园','睢阳','民权县','睢　县','宁陵县','柘城县','虞城县','夏邑县','永城']},
{name:'信阳', areaList:['辖','师河','平桥','罗山县','光山县','新　县','商城县','固始县','潢川县','淮滨县','息　县']},
{name:'周口', areaList:['辖','川汇','扶沟县','西华县','商水县','沈丘县','郸城县','淮阳县','太康县','鹿邑县','项城']},
{name:'驻马店', areaList:['辖','驿城','西平县','上蔡县','平舆县','正阳县','确山县','泌阳县','汝南县','遂平县','新蔡县']}
]},
{name:'湖北', cityList:[
{name:'武汉', areaList:['辖','江岸','江汉','乔口','汉阳','武昌','青山','洪山','东西湖','汉南','蔡甸','江夏','黄陂','新洲']},
{name:'黄石', areaList:['辖','黄石港','西塞山','下陆','铁山','阳新县','大冶']},
{name:'十堰', areaList:['辖','茅箭','张湾','郧　县','郧西县','竹山县','竹溪县','房　县','丹江口']},
{name:'宜昌', areaList:['辖','西陵','伍家岗','点军','猇亭','夷陵','远安县','兴山县','秭归县','长阳土家族自治县','五峰土家族自治县','宜都','当阳','枝江']},
{name:'襄樊', areaList:['辖','襄城','樊城','襄阳','南漳县','谷城县','保康县','老河口','枣阳','宜城']},
{name:'鄂州', areaList:['辖','梁子湖','华容','鄂城']},
{name:'荆门', areaList:['辖','东宝','掇刀','京山县','沙洋县','钟祥']},
{name:'孝感', areaList:['辖','孝南','孝昌县','大悟县','云梦县','应城','安陆','汉川']},
{name:'荆州', areaList:['辖','沙','荆州','公安县','监利县','江陵县','石首','洪湖','松滋']},
{name:'黄冈', areaList:['辖','黄州','团风县','红安县','罗田县','英山县','浠水县','蕲春县','黄梅县','麻城','武穴']},
{name:'咸宁', areaList:['辖','咸安','嘉鱼县','通城县','崇阳县','通山县','赤壁']},
{name:'随州', areaList:['辖','曾都','广水']},
{name:'恩施土家族苗族自治州', areaList:['恩施','利川','建始县','巴东县','宣恩县','咸丰县','来凤县','鹤峰县']},
{name:'省直辖行政单位', areaList:['仙桃','潜江','天门','神农架林']}
]},
{name:'湖南', cityList:[
{name:'长沙', areaList:['辖','芙蓉','天心','岳麓','开福','雨花','长沙县','望城县','宁乡县','浏阳']},
{name:'株洲', areaList:['辖','荷塘','芦淞','石峰','天元','株洲县','攸　县','茶陵县','炎陵县','醴陵']},
{name:'湘潭', areaList:['辖','雨湖','岳塘','湘潭县','湘乡','韶山']},
{name:'衡阳', areaList:['辖','珠晖','雁峰','石鼓','蒸湘','南岳','衡阳县','衡南县','衡山县','衡东县','祁东县','耒阳','常宁']},
{name:'邵阳', areaList:['辖','双清','大祥','北塔','邵东县','新邵县','邵阳县','隆回县','洞口县','绥宁县','新宁县','城步苗族自治县','武冈']},
{name:'岳阳', areaList:['辖','岳阳楼','云溪','君山','岳阳县','华容县','湘阴县','平江县','汨罗','临湘']},
{name:'常德', areaList:['辖','武陵','鼎城','安乡县','汉寿县','澧　县','临澧县','桃源县','石门县','津']},
{name:'张家界', areaList:['辖','永定','武陵源','慈利县','桑植县']},
{name:'益阳', areaList:['辖','资阳','赫山','南　县','桃江县','安化县','沅江']},
{name:'郴州', areaList:['辖','北湖','苏仙','桂阳县','宜章县','永兴县','嘉禾县','临武县','汝城县','桂东县','安仁县','资兴']},
{name:'永州', areaList:['辖','芝山','冷水滩','祁阳县','东安县','双牌县','道　县','江永县','宁远县','蓝山县','新田县','江华瑶族自治县']},
{name:'怀化', areaList:['辖','鹤城','中方县','沅陵县','辰溪县','溆浦县','会同县','麻阳苗族自治县','新晃侗族自治县','芷江侗族自治县','靖州苗族侗族自治县','通道侗族自治县','洪江']},
{name:'娄底', areaList:['辖','娄星','双峰县','新化县','冷水江','涟源']},
{name:'湘西土家族苗族自治州', areaList:['吉首','泸溪县','凤凰县','花垣县','保靖县','古丈县','永顺县','龙山县']}
]},
{name:'广东', cityList:[
{name:'广州', areaList:['辖','东山','荔湾','越秀','海珠','天河','芳村','白云','黄埔','番禺','花都','增城','从化']},
{name:'韶关', areaList:['辖','武江','浈江','曲江','始兴县','仁化县','翁源县','乳源瑶族自治县','新丰县','乐昌','南雄']},
{name:'深圳', areaList:['辖','罗湖','福田','南山','宝安','龙岗','盐田']},
{name:'珠海', areaList:['辖','香洲','斗门','金湾']},
{name:'汕头', areaList:['辖','龙湖','金平','濠江','潮阳','潮南','澄海','南澳县']},
{name:'佛山', areaList:['辖','禅城','南海','顺德','三水','高明']},
{name:'江门', areaList:['辖','蓬江','江海','新会','台山','开平','鹤山','恩平']},
{name:'湛江', areaList:['辖','赤坎','霞山','坡头','麻章','遂溪县','徐闻县','廉江','雷州','吴川']},
{name:'茂名', areaList:['辖','茂南','茂港','电白县','高州','化州','信宜']},
{name:'肇庆', areaList:['辖','端州','鼎湖','广宁县','怀集县','封开县','德庆县','高要','四会']},
{name:'惠州', areaList:['辖','惠城','惠阳','博罗县','惠东县','龙门县']},
{name:'梅州', areaList:['辖','梅江','梅　县','大埔县','丰顺县','五华县','平远县','蕉岭县','兴宁']},
{name:'汕尾', areaList:['辖','城　','海丰县','陆河县','陆丰']},
{name:'河源', areaList:['辖','源城','紫金县','龙川县','连平县','和平县','东源县']},
{name:'阳江', areaList:['辖','江城','阳西县','阳东县','阳春']},
{name:'清远', areaList:['辖','清城','佛冈县','阳山县','连山壮族瑶族自治县','连南瑶族自治县','清新县','英德','连州']},
{name:'东莞', areaList:['东莞']},
{name:'中山', areaList:['中山']},
{name:'潮州', areaList:['辖','湘桥','潮安县','饶平县']},
{name:'揭阳', areaList:['辖','榕城','揭东县','揭西县','惠来县','普宁']},
{name:'云浮', areaList:['辖','云城','新兴县','郁南县','云安县','罗定']}
]},
{name:'广西', cityList:[
{name:'南宁', areaList:['辖','兴宁','青秀','江南','西乡塘','良庆','邕宁','武鸣县','隆安县','马山县','上林县','宾阳县','横　县']},
{name:'柳州', areaList:['辖','城中','鱼峰','柳南','柳北','柳江县','柳城县','鹿寨县','融安县','融水苗族自治县','三江侗族自治县']},
{name:'桂林', areaList:['辖','秀峰','叠彩','象山','七星','雁山','阳朔县','临桂县','灵川县','全州县','兴安县','永福县','灌阳县','龙胜各族自治县','资源县','平乐县','荔蒲县','恭城瑶族自治县']},
{name:'梧州', areaList:['辖','万秀','蝶山','长洲','苍梧县','藤　县','蒙山县','岑溪']},
{name:'北海', areaList:['辖','海城','银海','铁山港','合浦县']},
{name:'防城港', areaList:['辖','港口','防城','上思县','东兴']},
{name:'钦州', areaList:['辖','钦南','钦北','灵山县','浦北县']},
{name:'贵港', areaList:['辖','港北','港南','覃塘','平南县','桂平']},
{name:'玉林', areaList:['辖','玉州','容　县','陆川县','博白县','兴业县','北流']},
{name:'百色', areaList:['辖','右江','田阳县','田东县','平果县','德保县','靖西县','那坡县','凌云县','乐业县','田林县','西林县','隆林各族自治县']},
{name:'贺州', areaList:['辖','八步','昭平县','钟山县','富川瑶族自治县']},
{name:'河池', areaList:['辖','金城江','南丹县','天峨县','凤山县','东兰县','罗城仫佬族自治县','环江毛南族自治县','巴马瑶族自治县','都安瑶族自治县','大化瑶族自治县','宜州']},
{name:'来宾', areaList:['辖','兴宾','忻城县','象州县','武宣县','金秀瑶族自治县','合山']},
{name:'崇左', areaList:['辖','江洲','扶绥县','宁明县','龙州县','大新县','天等县','凭祥']}
]},
{name:'陕西', cityList:[
{name:'西安', areaList:['辖','新城','碑林','莲湖','灞桥','未央','雁塔','阎良','临潼','长安','蓝田县','周至县','户　县','高陵县']},
{name:'铜川', areaList:['辖','王益','印台','耀州','宜君县']},
{name:'宝鸡', areaList:['辖','渭滨','金台','陈仓','凤翔县','岐山县','扶风县','眉　县','陇　县','千阳县','麟游县','凤　县','太白县']},
{name:'咸阳', areaList:['辖','秦都','杨凌','渭城','三原县','泾阳县','乾　县','礼泉县','永寿县','彬　县','长武县','旬邑县','淳化县','武功县','兴平']},
{name:'渭南', areaList:['辖','临渭','华　县','潼关县','大荔县','合阳县','澄城县','蒲城县','白水县','富平县','韩城','华阴']},
{name:'延安', areaList:['辖','宝塔','延长县','延川县','子长县','安塞县','志丹县','吴旗县','甘泉县','富　县','洛川县','宜川县','黄龙县','黄陵县']},
{name:'汉中', areaList:['辖','汉台','南郑县','城固县','洋　县','西乡县','勉　县','宁强县','略阳县','镇巴县','留坝县','佛坪县']},
{name:'榆林', areaList:['辖','榆阳','神木县','府谷县','横山县','靖边县','定边县','绥德县','米脂县','佳　县','吴堡县','清涧县','子洲县']},
{name:'安康', areaList:['辖','汉滨','汉阴县','石泉县','宁陕县','紫阳县','岚皋县','平利县','镇坪县','旬阳县','白河县']},
{name:'商洛', areaList:['辖','商州','洛南县','丹凤县','商南县','山阳县','镇安县','柞水县']}
]},
{name:'甘肃', cityList:[
{name:'兰州', areaList:['辖','城关','七里河','西固','安宁','红古','永登县','皋兰县','榆中县']},
{name:'嘉峪关', areaList:['辖']},
{name:'金昌', areaList:['辖','金川','永昌县']},
{name:'白银', areaList:['辖','白银','平川','靖远县','会宁县','景泰县']},
{name:'天水', areaList:['辖','秦城','北道','清水县','秦安县','甘谷县','武山县','张家川回族自治县']},
{name:'武威', areaList:['辖','凉州','民勤县','古浪县','天祝藏族自治县']},
{name:'张掖', areaList:['辖','甘州','肃南裕固族自治县','民乐县','临泽县','高台县','山丹县']},
{name:'平凉', areaList:['辖','崆峒','泾川县','灵台县','崇信县','华亭县','庄浪县','静宁县']},
{name:'酒泉', areaList:['辖','肃州','金塔县','安西县','肃北蒙古族自治县','阿克塞哈萨克族自治县','玉门','敦煌']},
{name:'庆阳', areaList:['辖','西峰','庆城县','环　县','华池县','合水县','正宁县','宁　县','镇原县']},
{name:'定西', areaList:['辖','安定','通渭县','陇西县','渭源县','临洮县','漳　县','岷　县']},
{name:'陇南', areaList:['辖','武都','成　县','文　县','宕昌县','康　县','西和县','礼　县','徽　县','两当县']},
{name:'临夏回族自治州', areaList:['临夏','临夏县','康乐县','永靖县','广河县','和政县','东乡族自治县','积石山保安族东乡族撒拉族自治县']},
{name:'甘南藏族自治州', areaList:['合作','临潭县','卓尼县','舟曲县','迭部县','玛曲县','碌曲县','夏河县']}
]},
{name:'青海', cityList:[
{name:'西宁', areaList:['辖','城东','城中','城西','城北','大通回族土族自治县','湟中县','湟源县']},
{name:'海东地', areaList:['平安县','民和回族土族自治县','乐都县','互助土族自治县','化隆回族自治县','循化撒拉族自治县']},
{name:'海北藏族自治州', areaList:['门源回族自治县','祁连县','海晏县','刚察县']},
{name:'黄南藏族自治州', areaList:['同仁县','尖扎县','泽库县','河南蒙古族自治县']},
{name:'海南藏族自治州', areaList:['共和县','同德县','贵德县','兴海县','贵南县']},
{name:'果洛藏族自治州', areaList:['玛沁县','班玛县','甘德县','达日县','久治县','玛多县']},
{name:'玉树藏族自治州', areaList:['玉树县','杂多县','称多县','治多县','囊谦县','曲麻莱县']},
{name:'海西蒙古族藏族自治州', areaList:['格尔木','德令哈','乌兰县','都兰县','天峻县']}
]},
{name:'宁夏', cityList:[
{name:'银川', areaList:['辖','兴庆','西夏','金凤','永宁县','贺兰县','灵武']},
{name:'石嘴山', areaList:['辖','大武口','惠农','平罗县']},
{name:'吴忠', areaList:['辖','利通','盐池县','同心县','青铜峡']},
{name:'固原', areaList:['辖','原州','西吉县','隆德县','泾源县','彭阳县','海原县']},
{name:'中卫', areaList:['辖','沙坡头','中宁县']}
]},
{name:'新疆', cityList:[
{name:'乌鲁木齐', areaList:['辖','天山','沙依巴克','新','水磨沟','头屯河','达坂城','东山','乌鲁木齐县']},
{name:'克拉玛依', areaList:['辖','独山子','克拉玛依','白碱滩','乌尔禾']},
{name:'吐鲁番地', areaList:['吐鲁番','鄯善县','托克逊县']},
{name:'哈密地', areaList:['哈密','巴里坤哈萨克自治县','伊吾县']},
{name:'昌吉回族自治州', areaList:['昌吉','阜康','米泉','呼图壁县','玛纳斯县','奇台县','吉木萨尔县','木垒哈萨克自治县']},
{name:'博尔塔拉蒙古自治州', areaList:['博乐','精河县','温泉县']},
{name:'巴音郭楞蒙古自治州', areaList:['库尔勒','轮台县','尉犁县','若羌县','且末县','焉耆回族自治县','和静县','和硕县','博湖县']},
{name:'阿克苏地', areaList:['阿克苏','温宿县','库车县','沙雅县','新和县','拜城县','乌什县','阿瓦提县','柯坪县']},
{name:'克孜勒苏柯尔克孜自治州', areaList:['阿图什','阿克陶县','阿合奇县','乌恰县']},
{name:'喀什地', areaList:['喀什','疏附县','疏勒县','英吉沙县','泽普县','莎车县','叶城县','麦盖提县','岳普湖县','伽师县','巴楚县','塔什库尔干塔吉克自治县']},
{name:'和田地', areaList:['和田','和田县','墨玉县','皮山县','洛浦县','策勒县','于田县','民丰县']},
{name:'伊犁哈萨克自治州', areaList:['伊宁','奎屯','伊宁县','察布查尔锡伯自治县','霍城县','巩留县','新源县','昭苏县','特克斯县','尼勒克县']},
{name:'塔城地', areaList:['塔城','乌苏','额敏县','沙湾县','托里县','裕民县','和布克赛尔蒙古自治县']},
{name:'阿勒泰地', areaList:['阿勒泰','布尔津县','富蕴县','福海县','哈巴河县','青河县','吉木乃县']},
{name:'省直辖行政单位', areaList:['石河子','阿拉尔','图木舒克','五家渠']}
]},
{name:'河北', cityList:[
{name:'石家庄', areaList:['辖','长安','桥东','桥西','新华','井陉矿','裕华','井陉县','正定县','栾城县','行唐县','灵寿县','高邑县','深泽县','赞皇县','无极县','平山县','元氏县','赵　县','辛集','藁城','晋州','新乐','鹿泉']},
{name:'唐山', areaList:['辖','路南','路北','古冶','开平','丰南','丰润','滦　县','滦南县','乐亭县','迁西县','玉田县','唐海县','遵化','迁安']},
{name:'秦皇岛', areaList:['辖','海港','山海关','北戴河','青龙满族自治县','昌黎县','抚宁县','卢龙县']},
{name:'邯郸', areaList:['辖','邯山','丛台','复兴','峰峰矿','邯郸县','临漳县','成安县','大名县','涉　县','磁　县','肥乡县','永年县','邱　县','鸡泽县','广平县','馆陶县','魏　县','曲周县','武安']},
{name:'邢台', areaList:['辖','桥东','桥西','邢台县','临城县','内丘县','柏乡县','隆尧县','任　县','南和县','宁晋县','巨鹿县','新河县','广宗县','平乡县','威　县','清河县','临西县','南宫','沙河']},
{name:'保定', areaList:['辖','新','北','南','满城县','清苑县','涞水县','阜平县','徐水县','定兴县','唐　县','高阳县','容城县','涞源县','望都县','安新县','易　县','曲阳县','蠡　县','顺平县','博野县','雄　县','涿州','定州','安国','高碑店']},
{name:'张家口', areaList:['辖','桥东','桥西','宣化','下花园','宣化县','张北县','康保县','沽源县','尚义县','蔚　县','阳原县','怀安县','万全县','怀来县','涿鹿县','赤城县','崇礼县']},
{name:'承德', areaList:['辖','双桥','双滦','鹰手营子矿','承德县','兴隆县','平泉县','滦平县','隆化县','丰宁满族自治县','宽城满族自治县','围场满族蒙古族自治县']},
{name:'沧州', areaList:['辖','新华','运河','沧　县','青　县','东光县','海兴县','盐山县','肃宁县','南皮县','吴桥县','献　县','孟村回族自治县','泊头','任丘','黄骅','河间']},
{name:'廊坊', areaList:['辖','安次','广阳','固安县','永清县','香河县','大城县','文安县','大厂回族自治县','霸州','三河']},
{name:'衡水', areaList:['辖','桃城','枣强县','武邑县','武强县','饶阳县','安平县','故城县','景　县','阜城县','冀州','深州']}
]},
{name:'山西', cityList:[
{name:'太原', areaList:['辖','小店','迎泽','杏花岭','尖草坪','万柏林','晋源','清徐县','阳曲县','娄烦县','古交']},
{name:'大同', areaList:['辖','城　','矿　','南郊','新荣','阳高县','天镇县','广灵县','灵丘县','浑源县','左云县','大同县']},
{name:'阳泉', areaList:['辖','城　','矿　','郊　','平定县','盂　县']},
{name:'长治', areaList:['辖','城　','郊　','长治县','襄垣县','屯留县','平顺县','黎城县','壶关县','长子县','武乡县','沁　县','沁源县','潞城']},
{name:'晋城', areaList:['辖','城　','沁水县','阳城县','陵川县','泽州县','高平']},
{name:'朔州', areaList:['辖','朔城','平鲁','山阴县','应　县','右玉县','怀仁县']},
{name:'晋中', areaList:['辖','榆次','榆社县','左权县','和顺县','昔阳县','寿阳县','太谷县','祁　县','平遥县','灵石县','介休']},
{name:'运城', areaList:['辖','盐湖','临猗县','万荣县','闻喜县','稷山县','新绛县','绛　县','垣曲县','夏　县','平陆县','芮城县','永济','河津']},
{name:'忻州', areaList:['辖','忻府','定襄县','五台县','代　县','繁峙县','宁武县','静乐县','神池县','五寨县','岢岚县','河曲县','保德县','偏关县','原平']},
{name:'临汾', areaList:['辖','尧都','曲沃县','翼城县','襄汾县','洪洞县','古　县','安泽县','浮山县','吉　县','乡宁县','大宁县','隰　县','永和县','蒲　县','汾西县','侯马','霍州']},
{name:'吕梁', areaList:['辖','离石','文水县','交城县','兴　县','临　县','柳林县','石楼县','岚　县','方山县','中阳县','交口县','孝义','汾阳']}
]},
{name:'内蒙古', cityList:[
{name:'呼和浩特', areaList:['辖','新城','回民','玉泉','赛罕','土默特左旗','托克托县','和林格尔县','清水河县','武川县']},
{name:'包头', areaList:['辖','东河','昆都仑','青山','石拐','白云矿','九原','土默特右旗','固阳县','达尔罕茂明安联合旗']},
{name:'乌海', areaList:['辖','海勃湾','海南','乌达']},
{name:'赤峰', areaList:['辖','红山','元宝山','松山','阿鲁科尔沁旗','巴林左旗','巴林右旗','林西县','克什克腾旗','翁牛特旗','喀喇沁旗','宁城县','敖汉旗']},
{name:'通辽', areaList:['辖','科尔沁','科尔沁左翼中旗','科尔沁左翼后旗','开鲁县','库伦旗','奈曼旗','扎鲁特旗','霍林郭勒']},
{name:'鄂尔多斯', areaList:['东胜','达拉特旗','准格尔旗','鄂托克前旗','鄂托克旗','杭锦旗','乌审旗','伊金霍洛旗']},
{name:'呼伦贝尔', areaList:['辖','海拉尔','阿荣旗','莫力达瓦达斡尔族自治旗','鄂伦春自治旗','鄂温克族自治旗','陈巴尔虎旗','新巴尔虎左旗','新巴尔虎右旗','满洲里','牙克石','扎兰屯','额尔古纳','根河']},
{name:'巴彦淖尔', areaList:['辖','临河','五原县','磴口县','乌拉特前旗','乌拉特中旗','乌拉特后旗','杭锦后旗']},
{name:'乌兰察布', areaList:['辖','集宁','卓资县','化德县','商都县','兴和县','凉城县','察哈尔右翼前旗','察哈尔右翼中旗','察哈尔右翼后旗','四子王旗','丰镇']},
{name:'兴安盟', areaList:['乌兰浩特','阿尔山','科尔沁右翼前旗','科尔沁右翼中旗','扎赉特旗','突泉县']},
{name:'锡林郭勒盟', areaList:['二连浩特','锡林浩特','阿巴嘎旗','苏尼特左旗','苏尼特右旗','东乌珠穆沁旗','西乌珠穆沁旗','太仆寺旗','镶黄旗','正镶白旗','正蓝旗','多伦县']},
{name:'阿拉善盟', areaList:['阿拉善左旗','阿拉善右旗','额济纳旗']}
]},
{name:'江苏', cityList:[
{name:'南京', areaList:['辖','玄武','白下','秦淮','建邺','鼓楼','下关','浦口','栖霞','雨花台','江宁','六合','溧水县','高淳县']},
{name:'无锡', areaList:['辖','崇安','南长','北塘','锡山','惠山','滨湖','江阴','宜兴']},
{name:'徐州', areaList:['辖','鼓楼','云龙','九里','贾汪','泉山','丰　县','沛　县','铜山县','睢宁县','新沂','邳州']},
{name:'常州', areaList:['辖','天宁','钟楼','戚墅堰','新北','武进','溧阳','金坛']},
{name:'苏州', areaList:['辖','沧浪','平江','金阊','虎丘','吴中','相城','常熟','张家港','昆山','吴江','太仓']},
{name:'南通', areaList:['辖','崇川','港闸','海安县','如东县','启东','如皋','通州','海门']},
{name:'连云港', areaList:['辖','连云','新浦','海州','赣榆县','东海县','灌云县','灌南县']},
{name:'淮安', areaList:['辖','清河','楚州','淮阴','清浦','涟水县','洪泽县','盱眙县','金湖县']},
{name:'盐城', areaList:['辖','亭湖','盐都','响水县','滨海县','阜宁县','射阳县','建湖县','东台','大丰']},
{name:'扬州', areaList:['辖','广陵','邗江','郊　','宝应县','仪征','高邮','江都']},
{name:'镇江', areaList:['辖','京口','润州','丹徒','丹阳','扬中','句容']},
{name:'泰州', areaList:['辖','海陵','高港','兴化','靖江','泰兴','姜堰']},
{name:'宿迁', areaList:['辖','宿城','宿豫','沭阳县','泗阳县','泗洪县']}
]},
{name:'浙江', cityList:[
{name:'杭州', areaList:['辖','上城','下城','江干','拱墅','西湖','滨江','萧山','余杭','桐庐县','淳安县','建德','富阳','临安']},
{name:'宁波', areaList:['辖','海曙','江东','江北','北仑','镇海','鄞州','象山县','宁海县','余姚','慈溪','奉化']},
{name:'温州', areaList:['辖','鹿城','龙湾','瓯海','洞头县','永嘉县','平阳县','苍南县','文成县','泰顺县','瑞安','乐清']},
{name:'嘉兴', areaList:['辖','秀城','秀洲','嘉善县','海盐县','海宁','平湖','桐乡']},
{name:'湖州', areaList:['辖','吴兴','南浔','德清县','长兴县','安吉县']},
{name:'绍兴', areaList:['辖','越城','绍兴县','新昌县','诸暨','上虞','嵊州']},
{name:'金华', areaList:['辖','婺城','金东','武义县','浦江县','磐安县','兰溪','义乌','东阳','永康']},
{name:'衢州', areaList:['辖','柯城','衢江','常山县','开化县','龙游县','江山']},
{name:'舟山', areaList:['辖','定海','普陀','岱山县','嵊泗县']},
{name:'台州', areaList:['辖','椒江','黄岩','路桥','玉环县','三门县','天台县','仙居县','温岭','临海']},
{name:'丽水', areaList:['辖','莲都','青田县','缙云县','遂昌县','松阳县','云和县','庆元县','景宁畲族自治县','龙泉']}
]},
{name:'安徽', cityList:[
{name:'合肥', areaList:['辖','瑶海','庐阳','蜀山','包河','长丰县','肥东县','肥西县']},
{name:'芜湖', areaList:['辖','镜湖','马塘','新芜','鸠江','芜湖县','繁昌县','南陵县']},
{name:'蚌埠', areaList:['辖','龙子湖','蚌山','禹会','淮上','怀远县','五河县','固镇县']},
{name:'淮南', areaList:['辖','大通','田家庵','谢家集','八公山','潘集','凤台县']},
{name:'马鞍山', areaList:['辖','金家庄','花山','雨山','当涂县']},
{name:'淮北', areaList:['辖','杜集','相山','烈山','濉溪县']},
{name:'铜陵', areaList:['辖','铜官山','狮子山','郊　','铜陵县']},
{name:'安庆', areaList:['辖','迎江','大观','郊　','怀宁县','枞阳县','潜山县','太湖县','宿松县','望江县','岳西县','桐城']},
{name:'黄山', areaList:['辖','屯溪','黄山','徽州','歙　县','休宁县','黟　县','祁门县']},
{name:'滁州', areaList:['辖','琅琊','南谯','来安县','全椒县','定远县','凤阳县','天长','明光']},
{name:'阜阳', areaList:['辖','颍州','颍东','颍泉','临泉县','太和县','阜南县','颍上县','界首']},
{name:'宿州', areaList:['辖','墉桥','砀山县','萧　县','灵璧县','泗　县']},
{name:'巢湖', areaList:['辖','居巢','庐江县','无为县','含山县','和　县']},
{name:'六安', areaList:['辖','金安','裕安','寿　县','霍邱县','舒城县','金寨县','霍山县']},
{name:'亳州', areaList:['辖','谯城','涡阳县','蒙城县','利辛县']},
{name:'池州', areaList:['辖','贵池','东至县','石台县','青阳县']},
{name:'宣城', areaList:['辖','宣州','郎溪县','广德县','泾　县','绩溪县','旌德县','宁国']}
]},
{name:'福建', cityList:[
{name:'福州', areaList:['辖','鼓楼','台江','仓山','马尾','晋安','闽侯县','连江县','罗源县','闽清县','永泰县','平潭县','福清','长乐']},
{name:'厦门', areaList:['辖','思明','海沧','湖里','集美','同安','翔安']},
{name:'莆田', areaList:['辖','城厢','涵江','荔城','秀屿','仙游县']},
{name:'三明', areaList:['辖','梅列','三元','明溪县','清流县','宁化县','大田县','尤溪县','沙　县','将乐县','泰宁县','建宁县','永安']},
{name:'泉州', areaList:['辖','鲤城','丰泽','洛江','泉港','惠安县','安溪县','永春县','德化县','金门县','石狮','晋江','南安']},
{name:'漳州', areaList:['辖','芗城','龙文','云霄县','漳浦县','诏安县','长泰县','东山县','南靖县','平和县','华安县','龙海']},
{name:'南平', areaList:['辖','延平','顺昌县','浦城县','光泽县','松溪县','政和县','邵武','武夷山','建瓯','建阳']},
{name:'龙岩', areaList:['辖','新罗','长汀县','永定县','上杭县','武平县','连城县','漳平']},
{name:'宁德', areaList:['辖','蕉城','霞浦县','古田县','屏南县','寿宁县','周宁县','柘荣县','福安','福鼎']}
]},
{name:'江西', cityList:[
{name:'南昌', areaList:['辖','东湖','西湖','青云谱','湾里','青山湖','南昌县','新建县','安义县','进贤县']},
{name:'景德镇', areaList:['辖','昌江','珠山','浮梁县','乐平']},
{name:'萍乡', areaList:['辖','安源','湘东','莲花县','上栗县','芦溪县']},
{name:'九江', areaList:['辖','庐山','浔阳','九江县','武宁县','修水县','永修县','德安县','星子县','都昌县','湖口县','彭泽县','瑞昌']},
{name:'新余', areaList:['辖','渝水','分宜县']},
{name:'鹰潭', areaList:['辖','月湖','余江县','贵溪']},
{name:'赣州', areaList:['辖','章贡','赣　县','信丰县','大余县','上犹县','崇义县','安远县','龙南县','定南县','全南县','宁都县','于都县','兴国县','会昌县','寻乌县','石城县','瑞金','南康']},
{name:'吉安', areaList:['辖','吉州','青原','吉安县','吉水县','峡江县','新干县','永丰县','泰和县','遂川县','万安县','安福县','永新县','井冈山']},
{name:'宜春', areaList:['辖','袁州','奉新县','万载县','上高县','宜丰县','靖安县','铜鼓县','丰城','樟树','高安']},
{name:'抚州', areaList:['辖','临川','南城县','黎川县','南丰县','崇仁县','乐安县','宜黄县','金溪县','资溪县','东乡县','广昌县']},
{name:'上饶', areaList:['辖','信州','上饶县','广丰县','玉山县','铅山县','横峰县','弋阳县','余干县','鄱阳县','万年县','婺源县','德兴']}
]},
{name:'山东', cityList:[
{name:'济南', areaList:['辖','历下','中','槐荫','天桥','历城','长清','平阴县','济阳县','商河县','章丘']},
{name:'青岛', areaList:['辖','南','北','四方','黄岛','崂山','李沧','城阳','胶州','即墨','平度','胶南','莱西']},
{name:'淄博', areaList:['辖','淄川','张店','博山','临淄','周村','桓台县','高青县','沂源县']},
{name:'枣庄', areaList:['辖','中','薛城','峄城','台儿庄','山亭','滕州']},
{name:'东营', areaList:['辖','东营','河口','垦利县','利津县','广饶县']},
{name:'烟台', areaList:['辖','芝罘','福山','牟平','莱山','长岛县','龙口','莱阳','莱州','蓬莱','招远','栖霞','海阳']},
{name:'潍坊', areaList:['辖','潍城','寒亭','坊子','奎文','临朐县','昌乐县','青州','诸城','寿光','安丘','高密','昌邑']},
{name:'济宁', areaList:['辖','中','任城','微山县','鱼台县','金乡县','嘉祥县','汶上县','泗水县','梁山县','曲阜','兖州','邹城']},
{name:'泰安', areaList:['辖','泰山','岱岳','宁阳县','东平县','新泰','肥城']},
{name:'威海', areaList:['辖','环翠','文登','荣成','乳山']},
{name:'日照', areaList:['辖','东港','岚山','五莲县','莒　县']},
{name:'莱芜', areaList:['辖','莱城','钢城']},
{name:'临沂', areaList:['辖','兰山','罗庄','河东','沂南县','郯城县','沂水县','苍山县','费　县','平邑县','莒南县','蒙阴县','临沭县']},
{name:'德州', areaList:['辖','德城','陵　县','宁津县','庆云县','临邑县','齐河县','平原县','夏津县','武城县','乐陵','禹城']},
{name:'聊城', areaList:['辖','东昌府','阳谷县','莘　县','茌平县','东阿县','冠　县','高唐县','临清']},
{name:'滨州', areaList:['辖','滨城','惠民县','阳信县','无棣县','沾化县','博兴县','邹平县']},
{name:'荷泽', areaList:['辖','牡丹','曹　县','单　县','成武县','巨野县','郓城县','鄄城县','定陶县','东明县']}
]},
{name:'辽宁', cityList:[
{name:'沈阳', areaList:['辖','和平','沈河','大东','皇姑','铁西','苏家屯','东陵','新城子','于洪','辽中县','康平县','法库县','新民']},
{name:'大连', areaList:['辖','中山','西岗','沙河口','甘井子','旅顺口','金州','长海县','瓦房店','普兰店','庄河']},
{name:'鞍山', areaList:['辖','铁东','铁西','立山','千山','台安县','岫岩满族自治县','海城']},
{name:'抚顺', areaList:['辖','新抚','东洲','望花','顺城','抚顺县','新宾满族自治县','清原满族自治县']},
{name:'本溪', areaList:['辖','平山','溪湖','明山','南芬','本溪满族自治县','桓仁满族自治县']},
{name:'丹东', areaList:['辖','元宝','振兴','振安','宽甸满族自治县','东港','凤城']},
{name:'锦州', areaList:['辖','古塔','凌河','太和','黑山县','义　县','凌海','北宁']},
{name:'营口', areaList:['辖','站前','西','鲅鱼圈','老边','盖州','大石桥']},
{name:'阜新', areaList:['辖','海州','新邱','太平','清河门','细河','阜新蒙古族自治县','彰武县']},
{name:'辽阳', areaList:['辖','白塔','文圣','宏伟','弓长岭','太子河','辽阳县','灯塔']},
{name:'盘锦', areaList:['辖','双台子','兴隆台','大洼县','盘山县']},
{name:'铁岭', areaList:['辖','银州','清河','铁岭县','西丰县','昌图县','调兵山','开原']},
{name:'朝阳', areaList:['辖','双塔','龙城','朝阳县','建平县','喀喇沁左翼蒙古族自治县','北票','凌源']},
{name:'葫芦岛', areaList:['辖','连山','龙港','南票','绥中县','建昌县','兴城']}
]},
{name:'吉林', cityList:[
{name:'长春', areaList:['辖','南关','宽城','朝阳','二道','绿园','双阳','农安县','九台','榆树','德惠']},
{name:'吉林', areaList:['辖','昌邑','龙潭','船营','丰满','永吉县','蛟河','桦甸','舒兰','磐石']},
{name:'四平', areaList:['辖','铁西','铁东','梨树县','伊通满族自治县','公主岭','双辽']},
{name:'辽源', areaList:['辖','龙山','西安','东丰县','东辽县']},
{name:'通化', areaList:['辖','东昌','二道江','通化县','辉南县','柳河县','梅河口','集安']},
{name:'白山', areaList:['辖','八道江','抚松县','靖宇县','长白朝鲜族自治县','江源县','临江']},
{name:'松原', areaList:['辖','宁江','前郭尔罗斯蒙古族自治县','长岭县','乾安县','扶余县']},
{name:'白城', areaList:['辖','洮北','镇赉县','通榆县','洮南','大安']},
{name:'延边朝鲜族自治州', areaList:['延吉','图们','敦化','珲春','龙井','和龙','汪清县','安图县']}
]},
{name:'黑龙江', cityList:[
{name:'哈尔滨', areaList:['辖','道里','南岗','道外','香坊','动力','平房','松北','呼兰','依兰县','方正县','宾　县','巴彦县','木兰县','通河县','延寿县','阿城','双城','尚志','五常']},
{name:'齐齐哈尔', areaList:['辖','龙沙','建华','铁锋','昂昂溪','富拉尔基','碾子山','梅里斯达斡尔族','龙江县','依安县','泰来县','甘南县','富裕县','克山县','克东县','拜泉县','讷河']},
{name:'鸡西', areaList:['辖','鸡冠','恒山','滴道','梨树','城子河','麻山','鸡东县','虎林','密山']},
{name:'鹤岗', areaList:['辖','向阳','工农','南山','兴安','东山','兴山','萝北县','绥滨县']},
{name:'双鸭山', areaList:['辖','尖山','岭东','四方台','宝山','集贤县','友谊县','宝清县','饶河县']},
{name:'大庆', areaList:['辖','萨尔图','龙凤','让胡路','红岗','大同','肇州县','肇源县','林甸县','杜尔伯特蒙古族自治县']},
{name:'伊春', areaList:['辖','伊春','南岔','友好','西林','翠峦','新青','美溪','金山屯','五营','乌马河','汤旺河','带岭','乌伊岭','红星','上甘岭','嘉荫县','铁力']},
{name:'佳木斯', areaList:['辖','永红','向阳','前进','东风','郊　','桦南县','桦川县','汤原县','抚远县','同江','富锦']},
{name:'七台河', areaList:['辖','新兴','桃山','茄子河','勃利县']},
{name:'牡丹江', areaList:['辖','东安','阳明','爱民','西安','东宁县','林口县','绥芬河','海林','宁安','穆棱']},
{name:'黑河', areaList:['辖','爱辉','嫩江县','逊克县','孙吴县','北安','五大连池']},
{name:'绥化', areaList:['辖','北林','望奎县','兰西县','青冈县','庆安县','明水县','绥棱县','安达','肇东','海伦']},
{name:'大兴安岭地', areaList:['呼玛县','塔河县','漠河县']}
]},
{name:'海南', cityList:[
{name:'海口', areaList:['辖','秀英','龙华','琼山','美兰']},
{name:'三亚', areaList:['辖']},
{name:'省直辖县级行政单位', areaList:['五指山','琼海','儋州','文昌','万宁','东方','定安县','屯昌县','澄迈县','临高县','白沙黎族自治县','昌江黎族自治县','乐东黎族自治县','陵水黎族自治县','保亭黎族苗族自治县','琼中黎族苗族自治县','西沙群岛','南沙群岛','中沙群岛的岛礁及其海域']}
]}
]