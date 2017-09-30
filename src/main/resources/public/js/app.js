window.onload = () => {

    function orderWishes(event) {
        $("#my-wishes").find("tbody")
            .find("tr")
            .each((trIndex, tr) => {
                $(tr).find("input")
                    .each((tdIndex, tdDom) => {
                            let td = $(tdDom);
                            td.prop("name", td.prop("name").replace(/\[\d+]/, `[${trIndex}]`));
                        })
            });
    }

    function assignRemoveWish() {
        let removeBtns = $(".remove-wish");
        removeBtns.off('click');
        removeBtns.click(e => {
            $(e.target).closest("tr").remove();
            orderWishes(e);
        });
    }

    function assignEditRemove() {
        assignRemoveWish();
    }

    assignEditRemove();

    $(".add-wish").click(e => {
        let appendedTr = $("#my-wishes").find("tbody").append(`
        <tr>
            <td class="wish-details">
                <input type="text" class="form-control wish-text" name="wishes[0].text" 
                    placeholder="Mam nadzieję, że pod choinką znajdę..."/>
                    <input type="text" class="form-control wish-url" name="wishes[0].url" 
                    placeholder="Adres URL"/>
            </td>
            <td>
                <input type="number" class="form-control" name="wishes[0].power" value="1" hidden="true"/>
                <div class="rate-wish">
                    <img class="power-tree opacity-tree" src="image/tree-min.png"/>
                </div>
            </td>
            <td>
                <div class="float-right">
                    <button type="button" class="btn btn-danger remove-wish">Usuń</button>
                </div>
            </td>
        </tr>`);
        $(appendedTr).find("input").focus();
        assignEditRemove();
        orderWishes();
        fillPowerTrees();
        rebindPower();
    });

    fillPowerTrees();
    rebindPower();

    function fillPowerTrees() {
        $(".rate-wish").each((idx, elem) => {
            let treeIcon = $(elem).find(".power-tree");

            if (treeIcon.length === 5) {
                return;
            }

            for (let i = 1; i < 5; i++) {
                let clone = treeIcon.clone();
                $(elem).append(clone);
            }
            let initialPower = $(elem).parent().find(".form-control").attr("value");
            $(elem).parent()
                .find(".power-tree")
                .slice(0, initialPower)
                .removeClass("opacity-tree");
        });
    }

    function rebindPower() {
        $(".rate-wish")
            .off("mouseleave")
            .on("mouseleave", e => {
                let initialPower = $(e.target).closest(".rate-wish").parent().find(".form-control").attr("value");
                $(e.target)
                    .closest(".rate-wish")
                    .find(".power-tree")
                    .addClass("opacity-tree")
                    .slice(0, initialPower)
                    .removeClass("opacity-tree");
            });

        $(".rate-wish .power-tree")
            .off("mouseenter mouseleave mouseclick")
            .on("mouseenter", function(e) {
                let x = e.offsetX, y = e.offsetY;
                colorNTrees(e.target, treeIndex(e.target));
            }).on("mouseleave", function(e) {
            let x = e.offsetX, y = e.offsetY;
            if (x === -1) {
                $(this).addClass("opacity-tree");
            }
        }).on("click", e => {
            $(e.target).closest(".rate-wish").parent().find(".form-control").attr("value", treeIndex(e.target));
        });
    }

    function treeIndex(treeElement) {
        return $(treeElement)
            .closest(".rate-wish")
            .find(".power-tree")
            .index($(treeElement)) + 1;

    }

    function colorNTrees(targetTree, n) {
        $(targetTree)
            .closest(".rate-wish")
            .find(".power-tree")
            .addClass("opacity-tree")
            .slice(0, n)
            .removeClass("opacity-tree");
    }
};